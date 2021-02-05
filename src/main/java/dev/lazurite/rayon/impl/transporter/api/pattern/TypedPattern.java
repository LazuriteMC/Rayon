package dev.lazurite.rayon.impl.transporter.api.pattern;

public interface TypedPattern<T> extends Pattern {
    T getIdentifier();
}
