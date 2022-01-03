package dev.lazurite.rayon.impl.bullet.math;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.mojang.math.Quaternion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Convert {
    public static AABB toMinecraft(BoundingBox box) {
        var min = box.getMin(new Vector3f());
        var max = box.getMax(new Vector3f());
        return new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static BoundingBox toBullet(AABB box) {
        return new BoundingBox(toBullet(box.getCenter()),
                (float) box.getXsize(),
                (float) box.getYsize(),
                (float) box.getZsize());
    }

    public static com.jme3.math.Quaternion toBullet(Quaternion quat) {
        return new com.jme3.math.Quaternion(quat.i(), quat.j(), quat.k(), quat.r());
    }

    public static Vector3f toBullet(BlockPos blockPos) {
        return new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
    }

    public static Quaternion toMinecraft(com.jme3.math.Quaternion quat) {
        return new Quaternion(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }

    public static com.mojang.math.Vector3f toMinecraft(Vector3f vector3f) {
        return new com.mojang.math.Vector3f(vector3f.x, vector3f.y, vector3f.z);
    }

    public static Vector3f toBullet(com.mojang.math.Vector3f vector3f) {
        return new Vector3f(vector3f.x(), vector3f.y(), vector3f.z());
    }

    public static Vector3f toBullet(Vec3 vec3) {
        return new Vector3f((float) vec3.x(), (float) vec3.y(), (float) vec3.z());
    }
}
