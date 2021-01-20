package dev.lazurite.rayon.impl.physics.body.shape;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.math.Vector3f;
import net.minecraft.util.math.Box;

/**
 * This class is basically just a wrapper for {@link BoxCollisionShape}. It's
 * meant to provide an easy way to create a {@link BoxCollisionShape} using
 * a bounding {@link Box} object.
 * @see BoxCollisionShape
 */
public class BoundingBoxShape extends BoxCollisionShape {
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
