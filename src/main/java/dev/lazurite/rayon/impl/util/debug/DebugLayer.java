package dev.lazurite.rayon.impl.util.debug;

public enum DebugLayer {
    ENTITY("debug.rayon.layer.entity"),
    BLOCK("debug.rayon.layer.block");

    private final String translation;

    DebugLayer(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return this.translation;
    }
}
