package dev.lazurite.api.util.math;

import net.minecraft.util.math.Vec3d;

import javax.vecmath.Vector3f;

public class VectorHelper {
    public static Vector3f vec3dToVector3f(Vec3d vec3d) {
        return new Vector3f((float) vec3d.x, (float) vec3d.y, (float) vec3d.z);
    }

    public static Vec3d vector3fToVec3d(Vector3f vector3f) {
        return new Vec3d(vector3f.x, vector3f.y, vector3f.z);
    }
}
