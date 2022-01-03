package dev.lazurite.rayon.impl.bullet.collision.body.shape;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.transporter.api.pattern.Pattern;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A shape made up of triangles to form a convex hull. Contains helper
 * methods to allow for easier creation (e.g. from a {@link AABB} or a {@link Pattern}).
 */
public class MinecraftShape extends HullCollisionShape {
    private final List<Triangle> triangles = new ArrayList<>();

    public MinecraftShape(List<Vector3f> triangles) {
        super(triangles);

        for (int i = 0; i < triangles.size(); i += 3) {
            this.triangles.add(new Triangle(triangles.get(i), triangles.get(i + 1), triangles.get(i + 2)));
        }
    }

    public List<Triangle> getTriangles(Quaternion quaternion) {
        return Collections.unmodifiableList(this.triangles.stream().map(triangle -> triangle.transform(quaternion)).toList());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MinecraftShape shape) {
            return shape.getTriangles(Quaternion.IDENTITY).equals(this.getTriangles(Quaternion.IDENTITY));
        }

        return false;
    }

    public static MinecraftShape of(AABB box) {
        return MinecraftShape.of(Convert.toBullet(box));
    }

    public static MinecraftShape of(BoundingBox box) {
        final var x = box.getXExtent() * 0.5f;
        final var y = box.getYExtent() * 0.5f;
        final var z = box.getZExtent() * 0.5f;

        final Vector3f[] points = {
                // south
                new Vector3f(x, y, z), new Vector3f(-x, y, z), new Vector3f(0, 0, z),
                new Vector3f(-x, y, z), new Vector3f(-x, -y, z), new Vector3f(0, 0, z),
                new Vector3f(-x, -y, z), new Vector3f(x, -y, z), new Vector3f(0, 0, z),
                new Vector3f(x, -y, z), new Vector3f(x, y, z), new Vector3f(0, 0, z),

                // north
                new Vector3f(-x, y, -z), new Vector3f(x, y, -z), new Vector3f(0, 0, -z),
                new Vector3f(x, y, -z), new Vector3f(x, -y, -z), new Vector3f(0, 0, -z),
                new Vector3f(x, -y, -z), new Vector3f(-x, -y, -z), new Vector3f(0, 0, -z),
                new Vector3f(-x, -y, -z), new Vector3f(-x, y, -z), new Vector3f(0, 0, -z),

                // east
                new Vector3f(x, y, -z), new Vector3f(x, y, z), new Vector3f(x, 0, 0),
                new Vector3f(x, y, z), new Vector3f(x, -y, z), new Vector3f(x, 0, 0),
                new Vector3f(x, -y, z), new Vector3f(x, -y, -z), new Vector3f(x, 0, 0),
                new Vector3f(x, -y, -z), new Vector3f(x, y, -z), new Vector3f(x, 0, 0),

                // west
                new Vector3f(-x, y, z), new Vector3f(-x, y, -z), new Vector3f(-x, 0, 0),
                new Vector3f(-x, y, -z), new Vector3f(-x, -y, -z), new Vector3f(-x, 0, 0),
                new Vector3f(-x, -y, -z), new Vector3f(-x, -y, z), new Vector3f(-x, 0, 0),
                new Vector3f(-x, -y, z), new Vector3f(-x, y, z), new Vector3f(-x, 0, 0),

                // up
                new Vector3f(x, y, -z), new Vector3f(-x, y, -z), new Vector3f(0, y, 0),
                new Vector3f(-x, y, -z), new Vector3f(-x, y, z), new Vector3f(0, y, 0),
                new Vector3f(-x, y, z), new Vector3f(x, y, z), new Vector3f(0, y, 0),
                new Vector3f(x, y, z), new Vector3f(x, y, -z), new Vector3f(0, y, 0),

                // down
                new Vector3f(x, -y, z), new Vector3f(-x, -y, z), new Vector3f(0, -y, 0),
                new Vector3f(-x, -y, z), new Vector3f(-x, -y, -z), new Vector3f(0, -y, 0),
                new Vector3f(-x, -y, -z), new Vector3f(x, -y, -z), new Vector3f(0, -y, 0),
                new Vector3f(x, -y, -z), new Vector3f(x, -y, z), new Vector3f(0, -y, 0)
        };

        return new MinecraftShape(Arrays.asList(points));
    }

    public static MinecraftShape of(Pattern pattern) {
        final var points = new ArrayList<Vector3f>();
        final int[] indices = { 0, 1, 2, 0, 3, 1 };

        for (var quad : pattern.getQuads()) {
            for (var index : indices) {
                points.add(Convert.toBullet(quad.getPoints().get(index)));
            }
        }

        return new MinecraftShape(points);
    }

    public static class Triangle {
        private final Vector3f[] vertices;
        private final Vector3f centroid;
        private final Vector3f area;

        public Triangle(Vector3f v1, Vector3f v2, Vector3f v3) {
            this.vertices = new Vector3f[] { v1, v2, v3 };
            this.centroid = new Vector3f().add(v1).add(v2).add(v3).divideLocal(3.0f);

            final var e1 = v1.subtract(v2);
            final var e2 = v2.subtract(v3);

            this.area = e2.cross(e1).multLocal(0.5f);
            this.area.multLocal(Math.signum(centroid.dot(area))); // make sure it faces outward
        }

        public Vector3f[] getVertices() {
            return this.vertices;
        }

        public Vector3f getCentroid() {
            return this.centroid;
        }

        public Vector3f getArea() {
            return this.area;
        }

        public Triangle transform(Quaternion quaternion) {
            return new Triangle(
                    transform(vertices[0].clone(), quaternion),
                    transform(vertices[1].clone(), quaternion),
                    transform(vertices[2].clone(), quaternion));
        }

        private static Vector3f transform(Vector3f vector, Quaternion quaternion) {
            final var mcVector = Convert.toMinecraft(vector);
            mcVector.transform(Convert.toMinecraft(quaternion));
            return Convert.toBullet(mcVector);
        }
    }
}