package dev.lazurite.rayon.impl.forge.dev;

import dev.lazurite.rayon.impl.dev.RayonDev;
import dev.lazurite.rayon.impl.dev.client.render.StoneBlockEntityModel;
import dev.lazurite.rayon.impl.dev.client.render.StoneBlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class RayonDevClientForge {
    public static void init() {
        EntityRenderers.register(RayonDev.STONE_BLOCK_ENTITY, (context) -> new StoneBlockEntityRenderer(context, new StoneBlockEntityModel(12, 4, 12)));
    }
}