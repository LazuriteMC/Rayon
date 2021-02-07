package dev.lazurite.rayon.impl.transporter.impl.buffer;

import dev.lazurite.rayon.impl.transporter.api.pattern.TypedPattern;

public class NetworkedPatternBuffer<T> extends AbstractPatternBuffer<T> {
    private boolean dirty;

    @Override
    public boolean put(TypedPattern<T> pattern) {
        return setDirty(super.put(pattern));
    }

    public void clear() {
        patterns.clear();
    }

    public boolean setDirty(boolean dirty) {
        this.dirty = dirty;
        return this.dirty;
    }

    public boolean isDirty() {
        return dirty;
    }
}

