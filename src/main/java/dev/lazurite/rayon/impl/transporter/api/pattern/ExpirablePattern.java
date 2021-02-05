package dev.lazurite.rayon.impl.transporter.api.pattern;

public interface ExpirablePattern extends Pattern {
    int getMaxAge();
    int getAge();
    void tick();
}
