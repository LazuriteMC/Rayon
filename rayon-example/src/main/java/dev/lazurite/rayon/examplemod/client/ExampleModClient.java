package dev.lazurite.rayon.examplemod.client;

import dev.lazurite.rayon.examplemod.ExampleMod;
import dev.lazurite.rayon.examplemod.client.render.CubeEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public class ExampleModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(ExampleMod.CUBE_ENTITY, (entityRenderDispatcher, context) -> new CubeEntityRenderer(entityRenderDispatcher));
    }
}
