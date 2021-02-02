package dev.lazurite.rayon.impl.transporter;

import net.minecraft.util.Identifier;

import java.util.HashMap;

public final class PatternBuffer extends HashMap<Identifier, Pattern> {
    private static final PatternBuffer instance = new PatternBuffer();

    private PatternBuffer() { }

    public static PatternBuffer getInstance() {
        return instance;
    }

    @Override
    public Pattern put(Identifier key, Pattern value) {
        if (!containsKey(key)) {
            return super.put(key, value);
        }

        return value;
    }
}
