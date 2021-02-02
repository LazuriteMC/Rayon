package dev.lazurite.rayon.impl.transporter;

import java.util.HashMap;

public final class PatternBuffer extends HashMap<Integer, Pattern> {
    private static final PatternBuffer instance = new PatternBuffer();

    private PatternBuffer() { }

    public static PatternBuffer getInstance() {
        return instance;
    }
}
