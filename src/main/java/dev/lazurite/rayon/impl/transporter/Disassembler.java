package dev.lazurite.rayon.impl.transporter;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Random;

public interface Disassembler {
    static Pattern getPattern(ItemStack itemStack) {
        Pattern pattern = new Pattern();
        MinecraftClient.getInstance().getItemRenderer()
                .renderItem(itemStack, ModelTransformation.Mode.GROUND, 0, 0, new MatrixStack(), pattern.asProvider());
        PatternBuffer.getInstance().put(Registry.ITEM.getId(itemStack.getItem()), pattern);
        return pattern;
    }

    static Pattern getPattern(BlockState blockState, World world) {
        Pattern pattern = new Pattern();
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate(-0.5, -0.5, -0.5);
        MinecraftClient.getInstance().getBlockRenderManager()
                .renderBlock(blockState, new BlockPos(0, 0, 0), world, matrixStack, pattern, false, new Random());
        PatternBuffer.getInstance().put(Registry.BLOCK.getId(blockState.getBlock()), pattern);
        matrixStack.pop();
        return pattern;
    }

    static Pattern getPattern(Entity entity) {
        Pattern pattern = new Pattern();
        MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity)
                .render(entity, 0, 0, new MatrixStack(), pattern.asProvider(), 0);
        PatternBuffer.getInstance().put(Registry.ENTITY_TYPE.getId(entity.getType()), pattern);
        return pattern;
    }
}
