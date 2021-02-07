package dev.lazurite.rayon.impl.transporter.impl.buffer;

import dev.lazurite.rayon.impl.transporter.impl.pattern.BufferEntry;

import java.util.List;

public class NetworkedPatternBuffer<T> extends AbstractPatternBuffer<T> {
    private boolean dirty;

    public void put(BufferEntry<T> pattern) {
        if (!patterns.contains(pattern)) {
            patterns.removeIf(entry -> entry.getIdentifier().equals((pattern).getIdentifier()));
            patterns.add(pattern);
            setDirty(true);
        }
    }

    public void putAll(List<BufferEntry<T>> patterns) {
        patterns.forEach(this::put);
    }

    public void clear() {
        patterns.clear();
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }
}

