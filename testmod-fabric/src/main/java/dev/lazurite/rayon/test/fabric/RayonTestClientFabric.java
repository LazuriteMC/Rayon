package dev.lazurite.rayon.test.fabric;

import dev.lazurite.rayon.test.RayonTest;
import dev.lazurite.rayon.test.client.render.StoneBlockEntityModel;
import dev.lazurite.rayon.test.client.render.StoneBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class RayonTestClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(RayonTest.STONE_BLOCK_ENTITY, (context) -> new StoneBlockEntityRenderer(context, new StoneBlockEntityModel(24, 24, 8)));
    }
}