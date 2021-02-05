package dev.lazurite.rayon.impl.transporter.impl.buffer;

import dev.lazurite.rayon.impl.transporter.impl.pattern.BufferEntry;

import java.util.List;

public class NetworkedPatternBuffer<T> extends AbstractPatternBuffer<T> {
    private boolean dirty;

    public void put(BufferEntry<T> pattern) {
        patterns.removeIf(entry -> entry.equals(pattern));
        patterns.add(pattern);
        dirty = true;
    }

    public void putAll(List<BufferEntry<T>> patterns) {
        this.patterns.removeIf(patterns::contains);
        this.patterns.addAll(patterns);
        dirty = true;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }
}

