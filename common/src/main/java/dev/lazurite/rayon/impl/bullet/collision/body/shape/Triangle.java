package dev.lazurite.rayon.impl.bullet.collision.body.shape;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.transporter.api.pattern.Pattern;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Triangle {
    private final Vector3f[] vertices;
    private final Vector3f centroid;
    private final Vector3f area;

    public static List<Triangle> getMeshOf(BoundingBox box) {
        final var x = box.getXExtent() * 0.5f;
        final var y = box.getYExtent() * 0.5f;
        final var z = box.getZExtent() * 0.5f;

        final var points = new Vector3f[] {
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

        final var triangles = new ArrayList<Triangle>();

        for (int i = 0; i < points.length; i += 3) {
            triangles.add(new Triangle(points[i], points[i + 1], points[i + 2]));
        }

        return triangles;
    }

    public static List<Triangle> getMeshOf(Pattern pattern) {
        final var triangles = new ArrayList<Triangle>();

        for (var quad : pattern.getQuads()) {
            final var centroid = new Vector3f();

            for (var point : quad.getPoints()) {
                centroid.addLocal(Convert.toBullet(point));
            }

            centroid.divideLocal(4);

            triangles.add(new Triangle(Convert.toBullet(quad.getPoints().get(0)), centroid, Convert.toBullet(quad.getPoints().get(1))));
            triangles.add(new Triangle(Convert.toBullet(quad.getPoints().get(1)), centroid, Convert.toBullet(quad.getPoints().get(2))));
            triangles.add(new Triangle(Convert.toBullet(quad.getPoints().get(2)), centroid, Convert.toBullet(quad.getPoints().get(3))));
            triangles.add(new Triangle(Convert.toBullet(quad.getPoints().get(3)), centroid, Convert.toBullet(quad.getPoints().get(0))));
        }

        return triangles;
    }

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
        return Convert.toBullet(
                Convert.toMinecraft(vector).mulTransposeDirection(
                        Convert.toMinecraft(quaternion).get(new Matrix4f())
                )
        );
    }
}