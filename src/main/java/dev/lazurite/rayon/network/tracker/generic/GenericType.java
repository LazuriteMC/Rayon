package dev.lazurite.rayon.network.tracker.generic;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.nbt.CompoundTag;

public interface GenericType<T> extends TrackedDataHandler<T> {
    void toTag(CompoundTag tag, String key, T value);
    T fromTag(CompoundTag tag, String key);

    Class<T> getClassType();
}
