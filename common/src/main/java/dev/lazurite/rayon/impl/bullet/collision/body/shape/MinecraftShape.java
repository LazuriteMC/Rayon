package dev.lazurite.rayon.impl.bullet.collision.body.shape;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.transporter.api.pattern.Pattern;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A shape made up of triangles to form a convex hull. Contains helper
 * methods to allow for easier creation (e.g. from a {@link AABB} or a {@link Pattern}).
 */
public class MinecraftShape extends HullCollisionShape {
    private final List<Vector3f> triangles;

    public MinecraftShape(List<Vector3f> triangles) {
        super(triangles);
        this.triangles = triangles;
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

    public List<Vector3f> getTriangles() {
        return this.triangles;
    }
}