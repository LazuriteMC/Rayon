package dev.lazurite.rayon.core.impl.util.debug;

import dev.lazurite.rayon.core.impl.bullet.body.type.DebuggableBody;

/**
 * The set of layers used in {@link DebugManager} and {@link DebuggableBody} objects.
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
