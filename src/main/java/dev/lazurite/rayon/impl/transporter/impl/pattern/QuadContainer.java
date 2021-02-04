package dev.lazurite.rayon.impl.transporter.impl.pattern;

import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.transporter.impl.pattern.part.Quad;

import java.util.List;

public class QuadContainer implements Pattern {
    private final List<Quad> quads;

    public QuadContainer(List<Quad> quads) {
        this.quads = quads;
    }

    @Override
    public List<Quad> getQuads() {
        return this.quads;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QuadContainer) {
            return ((QuadContainer) obj).getQuads().equals(getQuads());
        }

        return false;
    }
}
