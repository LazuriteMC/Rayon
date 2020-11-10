package dev.lazurite.api.network.tracker.generic.types;

import dev.lazurite.api.network.tracker.Config;
import dev.lazurite.api.network.tracker.generic.GenericType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public class IntegerType implements GenericType<Integer> {
    public void write(PacketByteBuf buf, Integer value) {
        buf.writeInt(value);
    }

    public Integer read(PacketByteBuf buf) {
        return buf.readInt();
    }

    public Integer copy(Integer integer) {
        return integer;
    }

    public void toTag(CompoundTag tag, String key, Integer value) {
        tag.putInt(key, value);
    }

    public Integer fromTag(CompoundTag tag, String key) {
        return tag.getInt(key);
    }

    public void toConfig(Config config, String key, Integer value) {
        config.setProperty(key, value.toString());
    }

    public Integer fromConfig(Config config, String key) {
        return Integer.parseInt(config.getProperty(key, "0"));
    }

    public Class<Integer> getClassType() {
        return int.class;
    }
}
