package dev.lazurite.rayon.core.impl;

import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.event.ClientEventHandler;
import dev.lazurite.rayon.core.impl.util.debug.CollisionObjectDebugger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class RayonCoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEventHandler.register();

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (CollisionObjectDebugger.getInstance().isEnabled()) {
                CollisionObjectDebugger.getInstance().renderSpace(MinecraftSpace.get(context.world()), context.tickDelta());
            }
        });
    }
}
