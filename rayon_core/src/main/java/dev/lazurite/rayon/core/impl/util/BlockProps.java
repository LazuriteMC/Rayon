package dev.lazurite.rayon.core.impl.util;

import dev.lazurite.rayon.core.impl.RayonCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = RayonCore.MODID)
public class BlockProps {
    public record BlockProperties(float friction, float restitution, boolean collidable) { }

    private static final Map<ResourceLocation, BlockProperties> blockProps = new HashMap<>();

    public static Map<ResourceLocation, BlockProperties> get() {
        return blockProps;
    }

    public static Optional<BlockProperties> get(ResourceLocation identifier) {
        return Optional.ofNullable(blockProps.get(identifier));
    }

    @SubscribeEvent
    public static void onImcProcess(InterModProcessEvent event){
        InterModComms.getMessages(RayonCore.MODID).forEach(imcMessage -> {
            String[] properties = ((String) imcMessage.messageSupplier().get()).split(" ");
            ResourceLocation block = new ResourceLocation(properties[0]);
            float friction = Float.parseFloat(properties[1]);
            float restitution = Float.parseFloat(properties[2]);
            boolean collidable = Boolean.parseBoolean(properties[3]);
            blockProps.put(block, new BlockProperties(friction, restitution, collidable));
        });
    }
}
