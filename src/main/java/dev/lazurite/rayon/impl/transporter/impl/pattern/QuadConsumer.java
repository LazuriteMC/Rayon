package dev.lazurite.rayon.impl.transporter.impl.pattern;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.transporter.impl.pattern.part.Quad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Vec3d;

import java.util.List;

@Environment(EnvType.CLIENT)
public class QuadConsumer implements VertexConsumer, Pattern {
    private final List<Quad> quads = Lists.newArrayList();
    private final List<Vec3d> points = Lists.newArrayList();

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        points.add(new Vec3d(x, y, z));
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

    public Provider asProvider() {
        return new Provider(this);
    }

    @Override
    public List<Quad> getQuads() {
        return this.quads;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QuadConsumer) {
            return ((QuadConsumer) obj).getQuads().equals(getQuads());
        }

        return false;
    }

    public static class Provider implements VertexConsumerProvider {
        private final QuadConsumer pattern;

        public Provider(QuadConsumer pattern) {
            this.pattern = pattern;
        }

        @Override
        public VertexConsumer getBuffer(RenderLayer layer) {
            return pattern;
        }
    }
}
