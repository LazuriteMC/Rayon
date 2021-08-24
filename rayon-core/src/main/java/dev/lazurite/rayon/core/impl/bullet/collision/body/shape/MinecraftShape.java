package dev.lazurite.rayon.core.impl.bullet.collision.body.shape;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.math.Converter;
import dev.lazurite.transporter.api.pattern.Pattern;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A shape made up of triangles to form a convex hull. Contains helper
 * methods to allow for easier creation (e.g. from a {@link Box} or a {@link Pattern}).
 */
public class MinecraftShape extends HullCollisionShape {
    private final List<Vector3f> triangles;

    public MinecraftShape(List<Vector3f> triangles) {
        super(triangles);
        this.triangles = triangles;
    }

    public static float getSignedTriangleVolume(Vector3f p1, Vector3f p2, Vector3f p3) {
        float v321 = p3.x * p2.y * p1.z;
        float v231 = p2.x * p3.y * p1.z;
        float v312 = p3.x * p1.y * p2.z;
        float v132 = p1.x * p3.y * p2.z;
        float v213 = p2.x * p1.y * p3.z;
        float v123 = p1.x * p2.y * p3.z;
        return (1.0f/6.0f) * (-v321 + v231 + v312 - v132 - v213 + v123);
    }

    public float getVolume() {
        var volume = 0.0f;

        for (var i = 0; i < triangles.size(); i += 3) {
            var pt1 = triangles.get(i);
            var pt2 = triangles.get(i + 1);
            var pt3 = triangles.get(i + 2);
//            var area = pt1.dot(pt2.cross(pt3));
            var vol = getSignedTriangleVolume(pt1, pt2, pt3);
            volume += vol;
        }

        return Math.abs(volume);
    }

    public List<Vector3f> getTriangles() {
        return this.triangles;
    }

    public static MinecraftShape of(Box box) {
        return MinecraftShape.of(Converter.toBullet(box));
    }

    public static MinecraftShape of(BoundingBox box) {
        var x = box.getXExtent() * 0.5f;
        var y = box.getYExtent() * 0.5f;
        var z = box.getZExtent() * 0.5f;

        Vector3f[] points = {
                // east
                new Vector3f(x, y, z), new Vector3f(-x, y, z), new Vector3f(-x, -y, z),
                new Vector3f(-x, -y, z), new Vector3f(x, -y, z), new Vector3f(x, y, z),

                // west
                new Vector3f(x, y, -z), new Vector3f(-x, y, -z), new Vector3f(-x, -y, -z),
                new Vector3f(-x, -y, -z), new Vector3f(x, -y, -z), new Vector3f(x, y, -z),

                // north
                new Vector3f(-x, y, z), new Vector3f(-x, -y, z), new Vector3f(-x, -y, -z),
                new Vector3f(-x, -y, -z), new Vector3f(-x, y, -z), new Vector3f(-x, y, z),

                // south
                new Vector3f(x, y, z), new Vector3f(x, -y, z), new Vector3f(x, -y, -z),
                new Vector3f(x, -y, -z), new Vector3f(x, y, -z), new Vector3f(x, y, z),

                // up
                new Vector3f(x, y, z), new Vector3f(-x, y, z), new Vector3f(-x, y, -z),
                new Vector3f(-x, y, -z), new Vector3f(x, y, -z), new Vector3f(x, y, z),

                // down
                new Vector3f(x, -y, z), new Vector3f(-x, -y, z), new Vector3f(-x, -y, -z),
                new Vector3f(-x, -y, -z), new Vector3f(x, -y, -z), new Vector3f(x, -y, z)
        };

        /*// abominable
        Vector3f[] points = {
            // south
            new Vector3f(-x, -y, z), new Vector3f(-x,  y,  z), new Vector3f(-x,  y, -z),
            new Vector3f(-x, -y, z), new Vector3f(-x, -y, -z), new Vector3f(-x,  y,  z),

            // north
            new Vector3f(x, -y, z), new Vector3f(x, y, z), new Vector3f(x, y, -z),
            new Vector3f(x, -y, z), new Vector3f(x, -y, -z), new Vector3f(x, y, z),

            // down
            new Vector3f(-x, -y, z), new Vector3f(x, -y, z), new Vector3f(x, -y, -z),
            new Vector3f(-x, -y, z), new Vector3f(-x, -y, -z), new Vector3f(x, -y, z),

            // up
            new Vector3f(-x, y, z), new Vector3f(x,  y,  z), new Vector3f(x, y, -z),
            new Vector3f(-x, y, z), new Vector3f(-x, y, -z), new Vector3f(x, y,  z),

            // west
            new Vector3f(-x, y, -z), new Vector3f(x,   y, -z), new Vector3f(x, -y, -z),
            new Vector3f(-x, y, -z), new Vector3f(-x, -y, -z), new Vector3f(x,  y, -z),

            // east
            new Vector3f(x, y, z), new Vector3f(-x, y, z), new Vector3f(x, -y, z),
            new Vector3f(x, y, z), new Vector3f(-x, -y, z), new Vector3f(-x, y, z)
        };*/

        return new MinecraftShape(Arrays.asList(points));
    }

    public static MinecraftShape of(Pattern pattern) {
        var points = new ArrayList<Vector3f>();
        int[] indices = { 0, 1, 2, 0, 3, 1 };

        for (var quad : pattern.getQuads()) {
            for (var index : indices) {
                points.add(Converter.toBullet(quad.getPoints().get(index)));
            }
        }

        return new MinecraftShape(points);
    }
}
