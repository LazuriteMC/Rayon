package dev.lazurite.toolbox.api;

import com.mojang.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * A useful helper for dealing with Minecraft vectors.
 */
public class VectorHelper {
    public static Vector3f toVector3f(Vec3 Vec3) {
        return new Vector3f((float) Vec3.x, (float) Vec3.y, (float) Vec3.z);
    }

    public static Vec3 toVec3(Vector3f vector3f) {
        return new Vec3(vector3f.x(), vector3f.y(), vector3f.z());
    }

    /**
     * Lerps two {@link Vector3f} objects using tick delta.
     * @param vec1 the first float vector
     * @param vec2 the second float vector
     * @param delta minecraft tick delta
     * @return the newly lerped {@link Vector3f}
     */
    public static Vector3f lerp(Vector3f vec1, Vector3f vec2, float delta) {
        return new Vector3f(
            Mth.lerp(delta, vec1.x(), vec2.x()),
            Mth.lerp(delta, vec1.y(), vec2.y()),
            Mth.lerp(delta, vec1.z(), vec2.z())
        );
    }

    /**
     * Lerps two {@link Vec3} objects using tick delta.
     * @param vec1 the first double vector
     * @param vec2 the second double vector
     * @param delta minecraft tick delta
     * @return the newly lerped {@link Vec3}
     */
    public static Vec3 lerp(Vec3 vec1, Vec3 vec2, float delta) {
        return new Vec3(
                Mth.lerp(delta, vec1.x, vec2.x),
                Mth.lerp(delta, vec1.y, vec2.y),
                Mth.lerp(delta, vec1.z, vec2.z)
        );
    }

    /**
     * Converts the given {@link Vector3f} into a new {@link CompoundTag}.
     * @param vec the {@link Vector3f} to convert
     * @return the new {@link CompoundTag}
     */
    public static CompoundTag toTag(Vector3f vec) {
        final var tag = new CompoundTag();
        tag.putFloat("x", vec.x());
        tag.putFloat("y", vec.y());
        tag.putFloat("z", vec.z());
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

    /**
     * Stuffs a {@link Vector3f} into a {@link FriendlyByteBuf}.
     * @param buf the buffer to stuff into
     * @param vec the vector to stuff
     */
    public static void toBuffer(FriendlyByteBuf buf, Vector3f vec) {
        buf.writeFloat(vec.x());
        buf.writeFloat(vec.y());
        buf.writeFloat(vec.z());
    }

    /**
     * Unpacks a {@link Vector3f} from a {@link FriendlyByteBuf}.
     * @param buf the buffer to unpack from
     * @return the newly unpacked vector
     */
    public static Vector3f fromBuffer(FriendlyByteBuf buf) {
        return new Vector3f(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
        );
    }
}
