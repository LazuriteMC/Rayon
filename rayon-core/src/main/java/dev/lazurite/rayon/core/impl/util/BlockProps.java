package dev.lazurite.rayon.core.impl.util;

import com.google.common.collect.Maps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.util.Map;

public class BlockProps {
    public record BlockProperties(float friction, float restitution, boolean collidable) { }

    private static final Map<Identifier, BlockProperties> blockProps = Maps.newHashMap();

    public static Map<Identifier, BlockProperties> get() {
        return blockProps;
    }

    public static void load() {
        FabricLoader.getInstance().getAllMods().forEach(mod -> {
            var modid = mod.getMetadata().getId();
            var rayon = mod.getMetadata().getCustomValue("rayon");

            if (rayon != null) {
                var blocks = rayon.getAsObject().get("blocks");

                if (blocks != null) {
                    blocks.getAsArray().forEach(block -> {
                        var name = block.getAsObject().get("name");

                        if (name != null) {
                            var friction = block.getAsObject().get("friction");
                            var restitution = block.getAsObject().get("restitution");
                            var collidable = block.getAsObject().get("collidable");

                            blockProps.put(new Identifier(modid, name.getAsString()), new BlockProperties(
                                    friction == null ? -1.0f : (float) (double) friction.getAsNumber(),
                                    restitution == null ? -1.0f : (float) (double) restitution.getAsNumber(),
                                    collidable == null || collidable.getAsBoolean()
                            ));
                        }
                    });
                }
            }
        });
    }
}
