package dev.lazurite.api.network.tracker.generic.types;

import dev.lazurite.api.network.tracker.Config;
import dev.lazurite.api.network.tracker.generic.GenericType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public class FloatType implements GenericType<Float> {
    public void write(PacketByteBuf buf, Float value) {
        buf.writeFloat(value);
    }

    public Float read(PacketByteBuf buf) {
        return buf.readFloat();
    }

    public Float copy(Float fl) {
        return fl;
    }

    public void toTag(CompoundTag tag, String key, Float value) {
        tag.putFloat(key, value);
    }

    public Float fromTag(CompoundTag tag, String key) {
        return tag.getFloat(key);
    }

    public void toConfig(Config config, String key, Float value) {
        config.setProperty(key, value.toString());
    }

    public Float fromConfig(Config config, String key) {
        return Float.parseFloat(config.getProperty(key, "0.0"));
    }

    public Class<Float> getClassType() {
        return float.class;
    }
}
