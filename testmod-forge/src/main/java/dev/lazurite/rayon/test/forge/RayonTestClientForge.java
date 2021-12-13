package dev.lazurite.rayon.test.forge;

import dev.lazurite.rayon.test.RayonTest;
import dev.lazurite.rayon.test.client.render.StoneBlockEntityModel;
import dev.lazurite.rayon.test.client.render.StoneBlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RayonTestClientForge {
    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        EntityRenderers.register(RayonTest.STONE_BLOCK_ENTITY, (context) -> new StoneBlockEntityRenderer(context, new StoneBlockEntityModel(32, 32, 8)));
    }
}