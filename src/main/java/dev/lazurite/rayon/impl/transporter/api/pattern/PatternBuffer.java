package dev.lazurite.rayon.impl.transporter.api.pattern;

import net.minecraft.util.Identifier;

import java.util.HashMap;

public final class PatternBuffer extends HashMap<Identifier, Pattern> {
    private static final PatternBuffer instance = new PatternBuffer();

    private PatternBuffer() { }

    public static PatternBuffer getInstance() {
        return instance;
    }

    @Override
    public Pattern get(Object key) {
        Pattern out = super.get(key);
        this.remove(key);
        return out;
    }

    @Override
    public Pattern put(Identifier identifier, Pattern pattern) {
        if (!containsKey(identifier)) {
            return super.put(identifier, pattern);
        }

        return pattern;
    }
}
