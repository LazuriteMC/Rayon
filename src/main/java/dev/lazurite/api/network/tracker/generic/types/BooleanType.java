package dev.lazurite.api.network.tracker.generic.types;

import dev.lazurite.api.network.tracker.generic.GenericType;
import dev.lazurite.api.network.tracker.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public class BooleanType implements GenericType<Boolean> {
    public void write(PacketByteBuf buf, Boolean value) {
        buf.writeBoolean(value);
    }

    public Boolean read(PacketByteBuf buf) {
        return buf.readBoolean();
    }

    public Boolean copy(Boolean bool) {
        return bool;
    }

    public void toTag(CompoundTag tag, String key, Boolean value) {
        tag.putBoolean(key, value);
    }

    public Boolean fromTag(CompoundTag tag, String key) {
        return tag.getBoolean(key);
    }

    public void toConfig(Config config, String key, Boolean value) {
        config.setProperty(key, value.toString());
    }

    public Boolean fromConfig(Config config, String key) {
        return Boolean.parseBoolean(config.getProperty(key, "false"));
    }

    public Class<Boolean> getClassType() {
        return boolean.class;
    }
}
