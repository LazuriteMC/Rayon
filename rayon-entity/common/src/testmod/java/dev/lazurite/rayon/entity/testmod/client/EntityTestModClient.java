package dev.lazurite.rayon.entity.testmod.client;

import dev.lazurite.rayon.entity.testmod.client.render.StoneBlockEntityModel;
import dev.lazurite.rayon.entity.testmod.client.render.StoneBlockEntityRenderer;
import dev.lazurite.rayon.entity.testmod.EntityTestMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class EntityTestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityTestMod.STONE_BLOCK_ENTITY, (context) -> new StoneBlockEntityRenderer(context, new StoneBlockEntityModel(8)));
    }
}
