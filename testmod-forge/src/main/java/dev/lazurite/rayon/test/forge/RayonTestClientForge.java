package dev.lazurite.rayon.test.forge;

import dev.lazurite.rayon.test.RayonTest;
import dev.lazurite.rayon.test.client.render.StoneBlockEntityModel;
import dev.lazurite.rayon.test.client.render.StoneBlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RayonTest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RayonTestClientForge {
    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        EntityRenderers.register(RayonTest.STONE_BLOCK_ENTITY, (context) -> new StoneBlockEntityRenderer(context, new StoneBlockEntityModel(12, 4, 12)));
    }
}