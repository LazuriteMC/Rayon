package dev.lazurite.rayon.util;

import dev.lazurite.thimble.synchronizer.type.SynchronizedType;
import dev.lazurite.thimble.synchronizer.type.SynchronizedTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * Definition of a couple extra data types that Thimble
 * doesn't come with. Includes:
 *    javax.vecmath.Vector3f
 *    javax.vecmath.Quat4f
 *
 * @author Ethan Johnson
 */
public class PhysicsTypes {
    public static final SynchronizedType<Vector3f> VECTOR3F = new SynchronizedType<Vector3f>() {
        @Override
        public void write(PacketByteBuf buf, Vector3f value) {
            buf.writeFloat(value.x);
            buf.writeFloat(value.y);
            buf.writeFloat(value.z);
        }

        @Override
        public Vector3f read(PacketByteBuf buf) {
            return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public void toTag(CompoundTag tag, String key, Vector3f value) {
            tag.putFloat(key + "x", value.x);
            tag.putFloat(key + "y", value.y);
            tag.putFloat(key + "z", value.z);
        }

        @Override
        public Vector3f fromTag(CompoundTag tag, String key) {
            return new Vector3f(tag.getFloat(key + "x"), tag.getFloat(key + "y"), tag.getFloat(key + "z"));
        }

        @Override
        public Vector3f copy(Vector3f value) {
            return new Vector3f(value);
        }

        @Override
        public Class<Vector3f> getClassType() {
            return Vector3f.class;
        }
    };

    public static final SynchronizedType<Quat4f> QUAT4F = new SynchronizedType<Quat4f>() {
        @Override
        public void write(PacketByteBuf buf, Quat4f value) {
            buf.writeFloat(value.x);
            buf.writeFloat(value.y);
            buf.writeFloat(value.z);
            buf.writeFloat(value.w);
        }

        @Override
        public Quat4f read(PacketByteBuf buf) {
            return new Quat4f(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public void toTag(CompoundTag tag, String key, Quat4f value) {
            tag.putFloat(key + "x", value.x);
            tag.putFloat(key + "y", value.y);
            tag.putFloat(key + "z", value.z);
            tag.putFloat(key + "w", value.w);
        }

        @Override
        public Quat4f fromTag(CompoundTag tag, String key) {
            return new Quat4f(tag.getFloat(key + "x"), tag.getFloat(key + "y"), tag.getFloat(key + "z"), tag.getFloat(key + "w"));
        }

        @Override
        public Quat4f copy(Quat4f value) {
            return new Quat4f(value);
        }

        @Override
        public Class<Quat4f> getClassType() {
            return Quat4f.class;
        }
    };

    static {
        SynchronizedTypeRegistry.register(VECTOR3F);
        SynchronizedTypeRegistry.register(QUAT4F);
    }
}
