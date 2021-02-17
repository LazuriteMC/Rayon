package dev.lazurite.rayon.impl;

import dev.lazurite.rayon.impl.element.entity.net.ElementPropertiesS2C;
import dev.lazurite.rayon.impl.element.entity.net.EntityElementMovementC2S;
import dev.lazurite.rayon.impl.element.entity.net.EntityElementMovementS2C;
import dev.lazurite.rayon.impl.util.NativeLoader;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.util.config.ConfigS2C;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main entrypoint for Rayon. Mainly contains packet registrations and also
 * contains the load call for {@link Config} and bullet natives.
 */
public class Rayon implements ModInitializer, ClientModInitializer, WorldComponentInitializer {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");
	public static final ComponentKey<PhysicsThread> THREAD = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "thread"), PhysicsThread.class);

	@Override
	public void onInitialize() {
		NativeLoader.load();
		Config.getInstance().load();
		ServerPlayNetworking.registerGlobalReceiver(EntityElementMovementC2S.PACKET_ID, EntityElementMovementC2S::accept);
	}

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ElementPropertiesS2C.PACKET_ID, ElementPropertiesS2C::accept);
		ClientPlayNetworking.registerGlobalReceiver(EntityElementMovementS2C.PACKET_ID, EntityElementMovementS2C::accept);
		ClientPlayNetworking.registerGlobalReceiver(ConfigS2C.PACKET_ID, ConfigS2C::accept);
	}

	/**
	 * Registers the {@link PhysicsThread} component in CCA.
	 * @param registry the cardinal components world registry
	 * @see PhysicsThread
	 */
	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(THREAD, PhysicsThread::new);
	}
}