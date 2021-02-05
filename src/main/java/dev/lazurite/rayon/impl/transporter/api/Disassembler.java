package dev.lazurite.rayon.impl.transporter.api;

import dev.lazurite.rayon.impl.transporter.api.buffer.BufferStorage;
import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.transporter.impl.pattern.BufferEntry;
import dev.lazurite.rayon.impl.transporter.impl.pattern.QuadConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@Environment(EnvType.CLIENT)
public interface Disassembler {
    static Pattern getBlock(BlockState blockState, BlockPos blockPos, World world) {
        QuadConsumer consumer = new QuadConsumer();
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(-0.5, -0.5, -0.5);
        MinecraftClient.getInstance().getBlockRenderManager()
                .renderBlock(blockState, blockPos, world, matrixStack, consumer, false, new Random());

        BufferEntry<BlockPos> pattern = new BufferEntry<>(consumer, blockPos);
        ((BufferStorage) world).getBlockBuffer().put(pattern);
        return pattern;
    }

    static Pattern getEntity(Entity entity, World world) {
        QuadConsumer consumer = new QuadConsumer();
        MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity)
                .render(entity, 0, 0, new MatrixStack(), consumer.asProvider(), 0);

        BufferEntry<Entity> pattern = new BufferEntry<>(consumer, entity);
        ((BufferStorage) world).getEntityBuffer().put(pattern);
        return pattern;
    }

    static Pattern getItem(Item item, World world) {
        QuadConsumer consumer = new QuadConsumer();
        MinecraftClient.getInstance().getItemRenderer()
                .renderItem(new ItemStack(item), ModelTransformation.Mode.GROUND, 0, 0, new MatrixStack(), consumer.asProvider());

        BufferEntry<Item> pattern = new BufferEntry<>(consumer, item);
        ((BufferStorage) world).getItemBuffer().put(pattern);
        return pattern;
    }

}
