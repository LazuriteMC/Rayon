package dev.lazurite.rayon.impl.transporter;

import com.google.common.collect.Lists;

import java.util.List;

public final class PatternBuffer {
    private static final PatternBuffer instance = new PatternBuffer();

    private final List<Pattern> patterns = Lists.newArrayList();

    private PatternBuffer() { }

    public static PatternBuffer getInstance() {
        return instance;
    }

    public void put(Pattern pattern) {
        this.patterns.add(pattern);
    }
}
