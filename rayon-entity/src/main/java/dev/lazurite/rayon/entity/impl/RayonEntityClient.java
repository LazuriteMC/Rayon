package dev.lazurite.rayon.entity.impl;

import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.net.ElementMovementS2C;
import dev.lazurite.rayon.entity.impl.net.ElementPropertiesS2C;
import dev.lazurite.rayon.core.impl.bullet.space.MinecraftSpace;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class RayonEntityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ElementPropertiesS2C.PACKET_ID, ElementPropertiesS2C::accept);
        ClientPlayNetworking.registerGlobalReceiver(ElementMovementS2C.PACKET_ID, ElementMovementS2C::accept);

        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof EntityPhysicsElement) {
                MinecraftSpace space = MinecraftSpace.get(world);
                space.getThread().execute(() -> space.load((EntityPhysicsElement) entity));
            }
        });

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof EntityPhysicsElement) {
                MinecraftSpace space = MinecraftSpace.get(world);
                space.getThread().execute(() -> space.unload((EntityPhysicsElement) entity));
            }
        });
    }
}
