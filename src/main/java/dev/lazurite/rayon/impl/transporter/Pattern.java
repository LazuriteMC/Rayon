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

    public List<Quad> getQuads() {
       return Lists.newArrayList(quads);
    }

    public Provider asProvider() {
        return new Provider(this);
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
        private final List<Vector3f> points;

        public Quad(List<Vector3f> points) {
            this.points = Lists.newArrayList(points);
        }

        public List<Vector3f> getPoints() {
            return Lists.newArrayList(points);
        }
    }
}
