package dev.lazurite.rayon.impl.transporter.impl.pattern;

import dev.lazurite.rayon.impl.transporter.api.pattern.ExpirablePattern;
import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.transporter.api.pattern.TypedPattern;
import dev.lazurite.rayon.impl.transporter.impl.pattern.part.Quad;

import java.util.List;

public class BufferEntry<T> implements TypedPattern<T>, ExpirablePattern {
    private final List<Quad> quads;
    private final T identifier;
    private int age;

    public BufferEntry(List<Quad> quads, T identifier) {
        this.quads = quads;
        this.identifier = identifier;
    }

    public BufferEntry(Pattern pattern, T identifier) {
        this(pattern.getQuads(), identifier);
    }

    @Override
    public T getIdentifier() {
        return identifier;
    }

    @Override
    public List<Quad> getQuads() {
        return quads;
    }

    @Override
    public void tick() {
        ++age;
    }

    @Override
    public int getMaxAge() {
        return 20;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BufferEntry) {
            BufferEntry pattern = (BufferEntry) obj;
            return pattern.getQuads().equals(getQuads()) && pattern.getIdentifier().equals(getIdentifier());
        }

        return false;
    }
}
