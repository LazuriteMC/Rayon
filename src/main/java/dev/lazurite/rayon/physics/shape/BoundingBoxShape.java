package dev.lazurite.rayon.physics.shape;

import com.bulletphysics.collision.shapes.BoxShape;
import net.minecraft.util.math.Box;

import javax.vecmath.Vector3f;

public class BoundingBoxShape extends BoxShape {
    private final Box box;

    public BoundingBoxShape(Box box) {
        super(new Vector3f(
                (float) (box.maxX - box.minX) / 2.5f,
                (float) (box.maxY - box.minY) / 2.5f,
                (float) (box.maxZ - box.minZ) / 2.5f));
        this.box = box;
    }

    public Box getBox() {
        return this.box;
    }
}
