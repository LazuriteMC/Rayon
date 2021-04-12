package dev.lazurite.rayon.core.impl.physics.debug;

import dev.lazurite.rayon.core.impl.physics.space.body.type.Debuggable;

/**
 * The set of layers used in {@link DebugManager} and {@link Debuggable} objects.
 */
public enum DebugLayer {
    BODY("debug.rayon.layer.body"),
    BLOCK("debug.rayon.layer.block");

    private final String translation;

    DebugLayer(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return this.translation;
    }
}
