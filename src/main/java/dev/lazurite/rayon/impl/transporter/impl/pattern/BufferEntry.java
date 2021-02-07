package dev.lazurite.rayon.impl.transporter.impl.pattern;

import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.transporter.api.pattern.TypedPattern;
import dev.lazurite.rayon.impl.transporter.impl.pattern.part.Quad;

import java.util.List;

public class BufferEntry<T> implements TypedPattern<T> {
    private final List<Quad> quads;
    private final T identifier;

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
    public boolean equals(Object obj) {
        if (obj instanceof BufferEntry) {
            return ((BufferEntry<?>) obj).getQuads().equals(getQuads());
        }

        return false;
    }
}
