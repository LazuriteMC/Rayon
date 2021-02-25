package dev.lazurite.rayon.impl.util.math;

import com.jme3.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * A helper class which contains useful operations and
 * functions specifically for vector objects.
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
     * Convert a bullet physics {@link Vector3f} to a minecraft {@link Vec3d}.
     * @param vector3f the {@link Vector3f} to convert
     * @return the new {@link Vec3d}
     */
    public static Vec3d vector3fToVec3d(Vector3f vector3f) {
        return new Vec3d(vector3f.x, vector3f.y, vector3f.z);
    }

    /**
     * Convert a bullet physics {@link Vector3f} to a minecraft {@link net.minecraft.client.util.math.Vector3f}.
     * @param vector3f the bullet physics {@link Vector3f}
     * @return the new minecraft {@link net.minecraft.client.util.math.Vector3f}
     */
    public static net.minecraft.client.util.math.Vector3f bulletToMinecraft(Vector3f vector3f) {
        return new net.minecraft.client.util.math.Vector3f(vector3f.x, vector3f.y, vector3f.z);
    }

    /**
     * Convert a minecraft {@link net.minecraft.client.util.math.Vector3f} to a bullet physics {@link Vector3f}.
     * @param vector3f the minecraft {@link net.minecraft.client.util.math.Vector3f}
     * @return the new bullet {@link Vector3f}
     */
    public static Vector3f minecraftToBullet(net.minecraft.client.util.math.Vector3f vector3f) {
        return new Vector3f(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    /**
     * Clamps the first argument between the second and third (min and max).
     * @param clamp the vector to clamp
     * @param min the minimum allowed vector
     * @param max the maximum allowed vector
     * @return the clamped vector
     */
    public static Vector3f clamp(Vector3f clamp, Vector3f min, Vector3f max) {
        clamp.x = Math.min(clamp.x, max.x);
        clamp.y = Math.min(clamp.y, max.y);
        clamp.z = Math.min(clamp.z, max.z);

        clamp.x = Math.max(clamp.x, min.x);
        clamp.y = Math.max(clamp.y, min.y);
        clamp.z = Math.max(clamp.z, min.z);

        return clamp;
    }

    /**
     * Lerps two bullet {@link Vector3f} objects using minecraft tick delta.
     * @param vec1 the first vector
     * @param vec2 the second vector
     * @param delta minecraft tick delta (time between ticks)
     * @return the lerped {@link Vector3f}
     */
    public static Vector3f lerp(Vector3f vec1, Vector3f vec2, float delta) {
        Vector3f out = new Vector3f();
        out.x = MathHelper.lerp(delta, vec1.x, vec2.x);
        out.y = MathHelper.lerp(delta, vec1.y, vec2.y);
        out.z = MathHelper.lerp(delta, vec1.z, vec2.z);
        return out;
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
}
