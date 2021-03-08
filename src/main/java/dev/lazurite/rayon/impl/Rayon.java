package dev.lazurite.rayon.impl;

import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.body.net.ElementMovementC2S;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.body.net.ElementPropertiesS2C;
import dev.lazurite.rayon.impl.bullet.body.net.ElementMovementS2C;
import dev.lazurite.rayon.impl.util.NativeLoader;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
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

	public static PhysicsThread CLIENT_THREAD;
	public static PhysicsThread SERVER_THREAD;

	@Override
	public void onInitialize() {
		NativeLoader.load();
		ServerPlayNetworking.registerGlobalReceiver(ElementMovementC2S.PACKET_ID, ElementMovementC2S::accept);
		ServerLifecycleEvents.SERVER_STARTING.register(server -> SERVER_THREAD = new PhysicsThread(server, "Server Physics Thread"));
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> SERVER_THREAD.destroy());
		ServerTickEvents.END_SERVER_TICK.register(server -> SERVER_THREAD.tick());

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof PhysicsElement) {
				SERVER_THREAD.execute(() -> Rayon.SPACE.get(world).addElement((PhysicsElement) entity));
			}
		});

		EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
			if (entity instanceof PhysicsElement) {
				SERVER_THREAD.execute(() -> Rayon.SPACE.get(entity.getEntityWorld()).addElement((PhysicsElement) entity));
			}
		});

		EntityTrackingEvents.STOP_TRACKING.register((entity, player) -> {
			if (entity instanceof PhysicsElement && PlayerLookup.tracking(entity).isEmpty()) {
				SERVER_THREAD.execute(() -> Rayon.SPACE.get(entity.getEntityWorld()).removeElement((PhysicsElement) entity));
			}
		});
	}

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ElementPropertiesS2C.PACKET_ID, ElementPropertiesS2C::accept);
		ClientPlayNetworking.registerGlobalReceiver(ElementMovementS2C.PACKET_ID, ElementMovementS2C::accept);
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> CLIENT_THREAD = new PhysicsThread(client, "Client Physics Thread"));
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> CLIENT_THREAD.destroy());
		ClientTickEvents.END_WORLD_TICK.register(client -> CLIENT_THREAD.tick());

		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof PhysicsElement) {
				CLIENT_THREAD.execute(() -> Rayon.SPACE.get(world).addElement((PhysicsElement) entity));
			}
		});

		ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			if (entity instanceof PhysicsElement) {
				CLIENT_THREAD.execute(() -> Rayon.SPACE.get(entity.getEntityWorld()).removeElement((PhysicsElement) entity));
			}
		});
	}

	/**
	 * Registers the {@link MinecraftSpace} component in CCA.
	 * @param registry the cardinal components world registry
	 * @see MinecraftSpace
	 */
	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(SPACE, world -> world.isClient() ? CLIENT_THREAD.createSpace(world) : SERVER_THREAD.createSpace(world));
	}
}