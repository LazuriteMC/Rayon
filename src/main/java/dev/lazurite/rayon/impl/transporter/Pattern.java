package dev.lazurite.rayon.impl.transporter;

import com.google.common.collect.Lists;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Vector3f;

import java.util.List;

public class Pattern implements VertexConsumer {
    private final List<Quad> quads = Lists.newArrayList();
    private final List<Vector3f> points = Lists.newArrayList();
    private final PatternType type;

    public Pattern(PatternType type) {
        this.type = type;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        points.add(new Vector3f((float) x, (float) y, (float) z));
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return this;
    }

    @Override
    public void next() {
        if (points.size() >= 4) {
            quads.add(new Quad(points));
            points.clear();
        }
    }

    public boolean isEmpty() {
        return this.quads.isEmpty();
    }

    public PatternType getType() {
        return this.type;
    }

    public List<Quad> getQuads() {
       return this.quads;
    }

    public Provider asProvider() {
        return new Provider(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pattern) {
            for (Quad quad1 : getQuads()) {
                for (Quad quad2 : ((Pattern) obj).getQuads()) {
                    if (!quad1.equals(quad2)) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    public static class Provider implements VertexConsumerProvider {
        private final Pattern pattern;

        public Provider(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public VertexConsumer getBuffer(RenderLayer layer) {
            return pattern;
        }
    }

    public static class Quad {
        private final Vector3f p1;
        private final Vector3f p2;
        private final Vector3f p3;
        private final Vector3f p4;

        public Quad(List<Vector3f> points) {
            this(points.get(0), points.get(1), points.get(2), points.get(3));
        }

        public Quad(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.p4 = p4;
        }

        public List<Vector3f> getPoints() {
            return Lists.newArrayList(p1, p2, p3, p4);
        }

        public Vector3f getCenterPoint() {
            float x = (p1.getX() + p2.getX() + p3.getX() + p4.getX()) / 4.0f;
            float y = (p1.getY() + p2.getY() + p3.getY() + p4.getY()) / 4.0f;
            float z = (p1.getZ() + p2.getZ() + p3.getZ() + p4.getZ()) / 4.0f;
            return new Vector3f(x, y, z);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Quad) {
                return getCenterPoint().equals(((Quad) obj).getCenterPoint());
            }

            return false;
        }
    }
}
