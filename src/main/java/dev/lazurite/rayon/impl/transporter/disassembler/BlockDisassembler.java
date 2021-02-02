package dev.lazurite.rayon.impl.transporter.disassembler;

import dev.lazurite.rayon.impl.transporter.Pattern;
import dev.lazurite.rayon.impl.transporter.PatternType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface BlockDisassembler {
    static Pattern getPattern(BlockState blockState, World world) {
        Pattern pattern = new Pattern(PatternType.BLOCK);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate(-0.5, -0.5, -0.5);
        MinecraftClient.getInstance().getBlockRenderManager()
                .renderBlock(blockState, new BlockPos(0, 0, 0), world, matrixStack, pattern, false, new Random());
        matrixStack.pop();
        return pattern;
    }
}
