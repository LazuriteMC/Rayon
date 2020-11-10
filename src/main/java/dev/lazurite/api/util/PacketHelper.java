package dev.lazurite.api.util;

import dev.lazurite.api.network.tracker.Config;
import net.minecraft.network.PacketByteBuf;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class PacketHelper {
    public static void serializeVector3f(PacketByteBuf buf, Vector3f vec) {
        buf.writeFloat(vec.x);
        buf.writeFloat(vec.y);
        buf.writeFloat(vec.z);
    }

    public static Vector3f deserializeVector3f(PacketByteBuf buf) {
        return new Vector3f(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public static void serializeQuaternion(PacketByteBuf buf, Quat4f q) {
        buf.writeFloat(q.x);
        buf.writeFloat(q.y);
        buf.writeFloat(q.z);
        buf.writeFloat(q.w);
    }

    public static Quat4f deserializeQuaternion(PacketByteBuf buf) {
        Quat4f q = new Quat4f();
        q.x = buf.readFloat();
        q.y = buf.readFloat();
        q.z = buf.readFloat();
        q.w = buf.readFloat();
        return q;
    }

    public static void serializeConfig(PacketByteBuf buf, Config config) {
        buf.writeInt(config.size());

        config.forEach((key, value) -> {
            buf.writeString((String) key, 200);
            buf.writeString((String) value, 200);
        });
    }

    public static Config deserializeConfig(PacketByteBuf buf) {
        Config config = new Config();

        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            config.setProperty(buf.readString(200), buf.readString(200));
        }

        return config;
    }
}
