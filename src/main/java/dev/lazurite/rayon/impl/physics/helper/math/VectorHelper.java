package dev.lazurite.rayon.impl.physics.helper.math;

import com.jme3.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

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
}
