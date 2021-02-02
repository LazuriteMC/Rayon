package dev.lazurite.rayon.impl.transporter;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public final class PatternBuffer extends HashMap<Identifier, Pattern> {
    private static final PatternBuffer instance = new PatternBuffer();

    private PatternBuffer() { }

    public static PatternBuffer getInstance() {
        return instance;
    }

    @Override
    public Pattern put(Identifier identifier, Pattern pattern) {
        if (!containsKey(identifier)) {
            if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) {
                PatternC2S.send(identifier, pattern);
            }

            return super.put(identifier, pattern);
        }

        return pattern;
    }
}
