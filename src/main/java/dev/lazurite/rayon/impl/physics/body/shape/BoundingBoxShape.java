package dev.lazurite.rayon.impl.physics.body.shape;

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
                (float) box.getXLength() / 2.0f,
                (float) box.getYLength() / 2.0f,
                (float) box.getZLength() / 2.0f));
        this.box = box;
    }

    public Box getBox() {
        return this.box;
    }
}
