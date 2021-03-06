package dev.lazurite.rayon.impl;

import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.element.entity.net.ElementPropertiesS2C;
import dev.lazurite.rayon.impl.element.entity.net.EntityElementMovementC2S;
import dev.lazurite.rayon.impl.element.entity.net.EntityElementMovementS2C;
import dev.lazurite.rayon.impl.util.NativeLoader;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main entrypoint for Rayon. Mainly contains packet
 * registrations and the loading of bullet natives.
 * @see NativeLoader
 */
public class Rayon implements ModInitializer, ClientModInitializer, WorldComponentInitializer {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");
	public static final ComponentKey<MinecraftSpace> SPACE = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "space"), MinecraftSpace.class);

	private static PhysicsThread clientThread;
	private static PhysicsThread serverThread;

	@Override
	public void onInitialize() {
		NativeLoader.load();
		ServerPlayNetworking.registerGlobalReceiver(EntityElementMovementC2S.PACKET_ID, EntityElementMovementC2S::accept);
		ServerLifecycleEvents.SERVER_STARTING.register(server -> serverThread = new PhysicsThread(server, "Server Physics Thread"));
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> serverThread.destroy());
		ServerTickEvents.END_SERVER_TICK.register(server -> serverThread.tick());
	}

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ElementPropertiesS2C.PACKET_ID, ElementPropertiesS2C::accept);
		ClientPlayNetworking.registerGlobalReceiver(EntityElementMovementS2C.PACKET_ID, EntityElementMovementS2C::accept);
		ClientPlayConnectionEvents.INIT.register((handler, client) -> clientThread = new PhysicsThread(client, "Client Physics Thread"));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clientThread.destroy());
		ClientTickEvents.END_WORLD_TICK.register(client -> clientThread.tick());
	}

	/**
	 * Registers the {@link PhysicsThread} component in CCA.
	 * @param registry the cardinal components world registry
	 * @see PhysicsThread
	 */
	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(SPACE, world -> world.isClient() ? clientThread.createSpace(world) : serverThread.createSpace(world));
	}
}