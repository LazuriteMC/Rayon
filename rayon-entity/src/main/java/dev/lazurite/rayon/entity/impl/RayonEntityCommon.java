package dev.lazurite.rayon.entity.impl;

import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.net.EntityElementMovementC2S;
import dev.lazurite.rayon.core.impl.thread.space.MinecraftSpace;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RayonEntityCommon implements ModInitializer {
	public static final String MODID = "rayon-entity";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Entity");

	@Override
	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(EntityElementMovementC2S.PACKET_ID, EntityElementMovementC2S::accept);

		PhysicsSpaceEvents.STEP.register(space -> {

		});

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof EntityPhysicsElement && !PlayerLookup.tracking(entity).isEmpty()) {
				MinecraftSpace space = MinecraftSpace.get(entity.getEntityWorld());
				space.getThread().execute(() -> space.load((EntityPhysicsElement) entity));
			}
		});

		EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
			if (entity instanceof EntityPhysicsElement) {
				MinecraftSpace space = MinecraftSpace.get(entity.getEntityWorld());
				space.getThread().execute(() -> space.load((EntityPhysicsElement) entity));
			}
		});

		EntityTrackingEvents.STOP_TRACKING.register((entity, player) -> {
			if (entity instanceof EntityPhysicsElement && PlayerLookup.tracking(entity).isEmpty()) {
				MinecraftSpace space = MinecraftSpace.get(entity.getEntityWorld());
				space.getThread().execute(() -> space.unload((EntityPhysicsElement) entity));
			}
		});
	}
}