package dev.lazurite.rayon.impl.transporter.api.buffer;

import dev.lazurite.rayon.impl.transporter.impl.buffer.NetworkedPatternBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public interface BufferStorage {
    NetworkedPatternBuffer<BlockPos> getBlockBuffer();
    NetworkedPatternBuffer<Entity> getEntityBuffer();
    NetworkedPatternBuffer<Item> getItemBuffer();
}
