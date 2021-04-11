package dev.lazurite.rayon.core.impl.util.math;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import net.minecraft.util.math.Box;

/**
 * Helper methods for converting between {@link Box}s and {@link BoundingBox}s.
 */
public class BoxHelper {
    public static Box bulletToMinecraft(BoundingBox box) {
        Vector3f min = box.getMin(new Vector3f());
        Vector3f max = box.getMax(new Vector3f());
        return new Box(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static BoundingBox minecraftToBullet(Box box) {
        return new BoundingBox(VectorHelper.vec3dToVector3f(box.getCenter()), (float) box.getXLength(), (float) box.getYLength(), (float) box.getZLength());
    }
}
