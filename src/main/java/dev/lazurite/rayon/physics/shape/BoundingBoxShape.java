package dev.lazurite.rayon.physics.shape;

import com.bulletphysics.collision.shapes.BoxShape;
import net.minecraft.util.math.Box;

import javax.vecmath.Vector3f;

/**
 * This class is basically just a wrapper for {@link BoxShape}. It's
 * meant to provide an easy way to create a {@link BoxShape} using
 * a bounding {@link Box} object.
 * @see BoxShape
 */
public class BoundingBoxShape extends BoxShape {
    private final Box box;

    public BoundingBoxShape(Box box) {
        super(new Vector3f(
                (float) (box.maxX - box.minX) / 1.75f,
                (float) (box.maxY - box.minY) / 1.75f,
                (float) (box.maxZ - box.minZ) / 1.75f));
        this.box = box;
    }

    public Box getBox() {
        return this.box;
    }
}
