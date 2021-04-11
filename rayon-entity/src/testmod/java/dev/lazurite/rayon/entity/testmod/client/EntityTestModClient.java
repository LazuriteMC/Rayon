package dev.lazurite.rayon.entity.testmod.client;

import dev.lazurite.rayon.entity.testmod.client.render.CubeEntityRenderer;
import dev.lazurite.rayon.entity.testmod.EntityTestMod;
import dev.lazurite.rayon.entity.testmod.client.render.model.CubeEntityModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public class EntityTestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(EntityTestMod.SMOL_CUBE_ENTITY, (entityRenderDispatcher, context) -> new CubeEntityRenderer(entityRenderDispatcher, new CubeEntityModel(8)));
        EntityRendererRegistry.INSTANCE.register(EntityTestMod.BIG_CUBE_ENTITY, (entityRenderDispatcher, context) -> new CubeEntityRenderer(entityRenderDispatcher, new CubeEntityModel(16)));
    }
}
