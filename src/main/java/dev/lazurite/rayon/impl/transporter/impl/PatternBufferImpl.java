package dev.lazurite.rayon.impl.transporter.impl;

import dev.lazurite.rayon.impl.transporter.api.pattern.PatternBuffer;
import dev.lazurite.rayon.impl.transporter.impl.packet.PatternC2S;
import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public final class PatternBufferImpl extends HashMap<Identifier, Pattern> implements PatternBuffer {
    private static final PatternBufferImpl instance = new PatternBufferImpl();

    private PatternBufferImpl() { }

    public static PatternBufferImpl getInstance() {
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
