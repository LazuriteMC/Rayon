package dev.lazurite.rayon.impl.bullet.collision.body.shape;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.transporter.api.pattern.Pattern;
import net.minecraft.world.phys.AABB;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public sealed interface MinecraftShape permits MinecraftShape.Convex, MinecraftShape.Concave {
    static Convex convex(AABB box) {
        return MinecraftShape.convex(Convert.toBullet(box));
    }

    static Convex convex(BoundingBox box) {
        return new Convex(Triangle.getMeshOf(box));
    }

    static Convex convex(Pattern pattern) {
        return new Convex(Triangle.getMeshOf(pattern));
    }

    static Concave concave(AABB box) {
        return MinecraftShape.concave(Convert.toBullet(box));
    }

    static Concave concave(BoundingBox box) {
        return new Concave(Triangle.getMeshOf(box));
    }

    static Concave concave(Pattern pattern) {
        return new Concave(Triangle.getMeshOf(pattern));
    }

    List<Triangle> getTriangles(Quaternion quaternion);

    final class Convex extends HullCollisionShape implements MinecraftShape {
        private final List<Triangle> triangles;

        public Convex(List<Triangle> triangles) {
            super(triangles.stream().flatMap(triangle -> Stream.of(triangle.getVertices())).toList());
            this.triangles = triangles;
        }

        @Override
        public List<Triangle> getTriangles(Quaternion quaternion) {
            return Collections.unmodifiableList(this.triangles.stream().map(triangle -> triangle.transform(quaternion)).toList());
        }
    }

    /* Less stable :( */
    final class Concave extends MeshCollisionShape implements MinecraftShape {
        private final List<Triangle> triangles;

        public Concave(List<Triangle> triangles) {
            super(false,
                ((Supplier<IndexedMesh>) () -> {
                    final var vertices = triangles.stream().flatMap(triangle -> Stream.of(triangle.getVertices())).toArray(Vector3f[]::new);
                    final var indices = new int[vertices.length];

                    for (var i = 0; i < vertices.length; i++) {
                        indices[i] = i;
                    }

                    return new IndexedMesh(vertices, indices);
                }
            ).get());
            this.triangles = triangles;
        }

        @Override
        public List<Triangle> getTriangles(Quaternion quaternion) {
            return Collections.unmodifiableList(this.triangles.stream().map(triangle -> triangle.transform(quaternion)).toList());
        }
    }
}