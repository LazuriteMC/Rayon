package dev.lazurite.rayon.impl.transporter.impl.buffer;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.impl.transporter.api.buffer.PatternBuffer;
import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.transporter.api.pattern.TypedPattern;
import dev.lazurite.rayon.impl.transporter.impl.pattern.BufferEntry;

import java.util.List;

public abstract class AbstractPatternBuffer<T> implements PatternBuffer<T> {
    protected final List<BufferEntry<T>> patterns = Lists.newArrayList();

    @Override
    public Pattern get(T identifier) {
        for (BufferEntry<T> pattern : patterns) {
            if (pattern.getIdentifier().equals(identifier)) {
                return pattern;
            }
        }

        return null;
    }

    @Override
    public List<TypedPattern<T>> getAll() {
        return Lists.newArrayList(patterns);
    }

    @Override
    public boolean contains(T key) {
        for (TypedPattern<T> pattern : patterns) {
            if (pattern.getIdentifier().equals(key)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int size() {
        return patterns.size();
    }
}
