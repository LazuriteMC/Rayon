package dev.lazurite.rayon.core.impl.util;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockProps {
    public record BlockProperties(float friction, float restitution, boolean collidable) { }

    private static final Map<ResourceLocation, BlockProperties> blockProps = new HashMap<>();

    public static Map<ResourceLocation, BlockProperties> get() {
        return blockProps;
    }

    public static Optional<BlockProperties> get(ResourceLocation identifier) {
        return Optional.ofNullable(blockProps.get(identifier));
    }

//    public static void load() {
//        FabricLoader.getInstance().getAllMods().forEach(mod -> {
//            final var modid = mod.getMetadata().getId();
//            final var rayon = mod.getMetadata().getCustomValue("rayon");
//
//            if (rayon != null) {
//                final var blocks = rayon.getAsObject().get("blocks");
//
//                if (blocks != null) {
//                    blocks.getAsArray().forEach(block -> {
//                        final var name = block.getAsObject().get("name");
//
//                        if (name != null) {
//                            final var friction = block.getAsObject().get("friction");
//                            final var restitution = block.getAsObject().get("restitution");
//                            final var collidable = block.getAsObject().get("collidable");
//
//                            blockProps.put(new ResourceLocation(modid, name.getAsString()), new BlockProperties(
//                                    friction == null ? -1.0f : (float) (double) friction.getAsNumber(),
//                                    restitution == null ? -1.0f : (float) (double) restitution.getAsNumber(),
//                                    collidable == null || collidable.getAsBoolean()
//                            ));
//                        }
//                    });
//                }
//            }
//        });
//    }
}
