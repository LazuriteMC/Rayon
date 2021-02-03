package dev.lazurite.rayon.impl.transporter.api.pattern;

import dev.lazurite.rayon.impl.transporter.impl.PatternBufferImpl;

public interface PatternBuffer {
    static PatternBuffer getInstance() {
        return PatternBufferImpl.getInstance();
    }
}
