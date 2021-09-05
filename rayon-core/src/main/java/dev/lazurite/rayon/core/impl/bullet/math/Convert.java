package dev.lazurite.rayon.core.impl.bullet.math;

import com.jme3.math.Vector3f;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class Convert {
    public static Box toMinecraft(com.jme3.bounding.BoundingBox box) {
        var min = box.getMin(new Vector3f());
        var max = box.getMax(new Vector3f());
        return new Box(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static com.jme3.bounding.BoundingBox toBullet(Box box) {
        return new com.jme3.bounding.BoundingBox(toBullet(box.getCenter()),
                (float) box.getXLength(),
                (float) box.getYLength(),
                (float) box.getZLength());
    }

    public static com.jme3.math.Quaternion toBullet(net.minecraft.util.math.Quaternion quat) {
        return new com.jme3.math.Quaternion(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }

    public static net.minecraft.util.math.Quaternion toMinecraft(com.jme3.math.Quaternion quat) {
        return new net.minecraft.util.math.Quaternion(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }

    public static Vec3f toMinecraft(Vector3f vector3f) {
        return new Vec3f(vector3f.x, vector3f.y, vector3f.z);
    }

    public static Vector3f toBullet(Vec3f vec3f) {
        return new Vector3f(vec3f.getX(), vec3f.getY(), vec3f.getZ());
    }

    public static Vector3f toBullet(Vec3d vec3d) {
        return new Vector3f((float) vec3d.getX(), (float) vec3d.getY(), (float) vec3d.getZ());
    }
}
