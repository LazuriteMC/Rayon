package dev.lazurite.rayon.core.impl.bullet.collision.body.shape;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.math.BoxHelper;
import dev.lazurite.rayon.core.impl.bullet.math.VectorHelper;
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

    public float getVolume() {
        var volume = 0.0f;

        for (var i = 0; i < triangles.size(); i += 3) {
            var p1 = triangles.get(i);
            var p2 = triangles.get(i + 1);
            var p3 = triangles.get(i + 2);
            volume += p1.dot(p2.multLocal(p3)) / 6.0f;
        }

        return volume;
    }

    public List<Vector3f> getTriangles() {
        return this.triangles;
    }

    public static MinecraftShape of(Box box) {
        return MinecraftShape.of(BoxHelper.minecraftToBullet(box));
    }

    public static MinecraftShape of(BoundingBox box) {
        var x = box.getXExtent() * 0.5f;
        var y = box.getYExtent() * 0.5f;
        var z = box.getZExtent() * 0.5f;

        // abominable
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
        };

        return new MinecraftShape(Arrays.asList(points));
    }

    public static MinecraftShape of(Pattern pattern) {
        var points = new ArrayList<Vector3f>();
        int[] indices = { 0, 1, 2, 0, 3, 1 };

        for (var quad : pattern.getQuads()) {
            for (var index : indices) {
                points.add(VectorHelper.vec3dToVector3f(quad.getPoints().get(index)));
            }
        }

        return new MinecraftShape(points);
    }
}
