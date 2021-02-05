package dev.lazurite.rayon.impl.mixin.transporter;

import dev.lazurite.rayon.impl.transporter.api.buffer.BufferStorage;
import dev.lazurite.rayon.impl.transporter.impl.buffer.NetworkedPatternBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(World.class)
public class WorldMixin implements BufferStorage {
    @Unique private final NetworkedPatternBuffer<BlockPos> blockBuffer = new NetworkedPatternBuffer<>();
    @Unique private final NetworkedPatternBuffer<Entity> entityBuffer = new NetworkedPatternBuffer<>();
    @Unique private final NetworkedPatternBuffer<Item> itemBuffer = new NetworkedPatternBuffer<>();

    @Unique @Override
    public NetworkedPatternBuffer<BlockPos> getBlockBuffer() {
        return blockBuffer;
    }

    @Unique @Override
    public NetworkedPatternBuffer<Entity> getEntityBuffer() {
        return entityBuffer;
    }

    @Unique @Override
    public NetworkedPatternBuffer<Item> getItemBuffer() {
        return itemBuffer;
    }
}
