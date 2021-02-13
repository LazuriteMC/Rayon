package dev.lazurite.rayon.examplemod.client;

import dev.lazurite.rayon.examplemod.ExampleMod;
import dev.lazurite.rayon.examplemod.client.render.BigRectangularPrismEntityRenderer;
import dev.lazurite.rayon.examplemod.client.render.LivingCubeEntityRenderer;
import dev.lazurite.rayon.examplemod.client.render.RectangularPrismEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public class ExampleModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(ExampleMod.RECTANGULAR_PRISM_ENTITY, (entityRenderDispatcher, context) -> new RectangularPrismEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(ExampleMod.BIG_RECTANGULAR_PRISM_ENTITY, (entityRenderDispatcher, context) -> new BigRectangularPrismEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(ExampleMod.LIVING_CUBE_ENTITY, (entityRenderDispatcher, context) -> new LivingCubeEntityRenderer(entityRenderDispatcher));
    }
}
