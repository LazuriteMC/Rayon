package dev.lazurite.rayon.core.impl.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.util.Arrays;
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

    public static void load() {
        ModList.get().getMods().forEach(mod -> {
            final var modid = mod.getModId();
            final var properties = mod.getModProperties();
            final var r = properties.get("rayon");

            if (r != null) {
                final var rayon = (Map<String, Object>)r;
                final var blocks = (Map<String, Object>[])rayon.get("blocks");

                if (blocks != null) {
                    Arrays.stream(blocks).forEach(block -> {
                        final var name = (String)block.get("name");

                        if (name != null) {
                            final var friction = block.get("friction");

                            final var restitution = block.get("restitution");
                            final var collidable = block.get("collidable");

                            blockProps.put(new ResourceLocation(modid, name), new BlockProperties(
                                    friction == null ? -1.0f : (float) (double) (Double) friction,
                                    restitution == null ? -1.0f : (float) (double) (Double) restitution,
                                    collidable == null || (Boolean) collidable
                            ));
                        }
                    });
                }
            }
        });
    }
}
