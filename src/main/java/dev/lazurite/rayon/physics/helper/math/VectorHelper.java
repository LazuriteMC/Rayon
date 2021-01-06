package dev.lazurite.rayon.physics.helper.math;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Vector3f;

/**
 * A helper class which contains useful operations and
 * functions specifically for {@link Vector3f} objects.
 */
public class VectorHelper {
    /**
     * Convert a minecraft {@link Vec3d} to a vecmath {@link Vector3f}.
     * @param vec3d the {@link Vec3d} to convert
     * @return the new {@link Vector3f}
     */
    public static Vector3f vec3dToVector3f(Vec3d vec3d) {
        return new Vector3f((float) vec3d.x, (float) vec3d.y, (float) vec3d.z);
    }

    /**
     * Convert a vecmath {@link Vector3f} to a minecraft {@link Vec3d}.
     * @param vector3f the {@link Vector3f} to convert
     * @return the new {@link Vec3d}
     */
    public static Vec3d vector3fToVec3d(Vector3f vector3f) {
        return new Vec3d(vector3f.x, vector3f.y, vector3f.z);
    }

    /**
     * Converts the given {@link Vector3f} into a new {@link CompoundTag}.
     * @param vector3f the {@link Vector3f} to convert
     * @return the new {@link CompoundTag}
     */
    public static CompoundTag toTag(Vector3f vector3f) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("x", vector3f.x);
        tag.putFloat("y", vector3f.y);
        tag.putFloat("z", vector3f.z);
        return tag;
    }

    /**
     * Retrieves a {@link Vector3f} from the given {@link CompoundTag}.
     * @param tag the {@link CompoundTag} to retrieve the {@link Vector3f} from
     * @return the new {@link Vector3f}
     */
    public static Vector3f fromTag(CompoundTag tag) {
        return new Vector3f(
                tag.getFloat("x"),
                tag.getFloat("y"),
                tag.getFloat("z")
        );
    }

    public static void toBuffer(PacketByteBuf buf, Vector3f vector3f) {
        buf.writeFloat(vector3f.x);
        buf.writeFloat(vector3f.y);
        buf.writeFloat(vector3f.z);
    }

    public static Vector3f fromBuffer(PacketByteBuf buf) {
        return new Vector3f(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat());
    }

    public static Vector3f add(Vector3f... vecs) {
        Vector3f out = new Vector3f();

        for (Vector3f vec : vecs) {
            out.add(vec);
        }

        return out;
    }

    public static Vector3f sub(Vector3f... vecs) {
        Vector3f out = new Vector3f();

        for (Vector3f vec : vecs) {
            out.sub(vec);
        }

        return out;
    }

    public static Vector3f mul(Vector3f v, float scale) {
        Vector3f out = new Vector3f(v);
        out.scale(scale);
        return out;
    }

    /**
     * Based on the following article: http://archive.gamedev.net/archive/reference/articles/article914.html
     * @param oldPos
     * @param newPos
     * @param oldVel
     * @param newVel
     * @param accel
     * @param t
     * @return
     */
    public static Vector3f spline(Vector3f oldPos, Vector3f newPos, Vector3f oldVel, Vector3f newVel, Vector3f accel, float t) {
        Vector3f v0 = new Vector3f(oldPos);
        Vector3f v1 = new Vector3f(add(oldPos, oldVel));
        Vector3f v2 = new Vector3f(add(newPos, mul(newVel, t), mul(accel, 0.5f * (t * t))));
        Vector3f v3 = new Vector3f(sub(v2, add(newVel, mul(accel, t))));

        float a = v3.x - (3 * v2.x) + (3 * v1.x) - v0.x;
        float b = (3 * v2.x) - (6 * v1.x) + (3 * v0.x);
        float c = (3 * v1.x) - (3 * v0.x);
        float d = v0.x;

        float e = v3.y - (3 * v2.y) + (3 * v1.y) - v0.y;
        float f = (3 * v2.y) - (6 * v1.y) + (3 * v0.y);
        float g = (3 * v1.y) - (3 * v0.y);
        float h = v0.y;

        float i = v3.z - (3 * v2.z) + (3 * v1.z) - v0.z;
        float j = (3 * v2.z) - (6 * v1.z) + (3 * v0.z);
        float k = (3 * v1.z) - (3 * v0.z);
        float l = v0.z;

        float x = (a * (t * t * t)) + (b * (t * t)) + (c * t) + d;
        float y = (e * (t * t * t)) + (f * (t * t)) + (g * t) + h;
        float z = (i * (t * t * t)) + (j * (t * t)) + (k * t) + l;

        return new Vector3f(x, y, z);
    }
}
