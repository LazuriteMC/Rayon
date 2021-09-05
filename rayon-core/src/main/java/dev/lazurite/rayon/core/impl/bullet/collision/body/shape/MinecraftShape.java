package dev.lazurite.rayon.core.impl.bullet.collision.body.shape;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
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
    public MinecraftShape(List<Vector3f> triangles) {
        super(triangles);
    }

    public static MinecraftShape of(Box box) {
        return MinecraftShape.of(Convert.toBullet(box));
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

        return new MinecraftShape(Arrays.asList(points));
    }

    public static MinecraftShape of(Pattern pattern) {
        var points = new ArrayList<Vector3f>();
        int[] indices = { 0, 1, 2, 0, 3, 1 };

        for (var quad : pattern.getQuads()) {
            for (var index : indices) {
                points.add(Convert.toBullet(quad.getPoints().get(index)));
            }
        }

        return new MinecraftShape(points);
    }
}
