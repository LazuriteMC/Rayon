package dev.lazurite.rayon.examplemod;

import dev.lazurite.rayon.examplemod.render.RectangularPrismEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class ExampleModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        EntityRendererRegistry.INSTANCE.register(ExampleMod.RECTANGULAR_PRISM_ENTITY, (entityRenderDispatcher, context) -> new RectangularPrismEntityRenderer(entityRenderDispatcher));
    }
}
