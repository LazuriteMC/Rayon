package dev.lazurite.rayon.impl.fabric.dev;

import dev.lazurite.rayon.impl.dev.RayonDev;
import dev.lazurite.rayon.impl.dev.client.render.StoneBlockEntityModel;
import dev.lazurite.rayon.impl.dev.client.render.StoneBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class RayonDevClientFabric {
    public static void init() {
        EntityRendererRegistry.register(RayonDev.STONE_BLOCK_ENTITY, (context) -> new StoneBlockEntityRenderer(context, new StoneBlockEntityModel(12, 4, 12)));
    }
}