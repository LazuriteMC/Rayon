package dev.lazurite.rayon.impl.transporter.impl.buffer;

import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.transporter.impl.pattern.BufferEntry;

import java.util.List;

public class NetworkedPatternBuffer<T> extends AbstractPatternBuffer<T> {
    private boolean dirty;

    @SuppressWarnings("unchecked")
    public void put(Pattern pattern) {
        if (!patterns.contains((BufferEntry<T>) pattern)) {
            patterns.removeIf(entry -> entry.getIdentifier().equals(((BufferEntry<T>) pattern).getIdentifier()));
            patterns.add((BufferEntry<T>) pattern);
            setDirty(true);
        }
    }

    public void putAll(List<BufferEntry<T>> patterns) {
        patterns.forEach(this::put);
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }
}

