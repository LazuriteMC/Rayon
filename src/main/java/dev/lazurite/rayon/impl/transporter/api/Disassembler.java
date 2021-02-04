package dev.lazurite.rayon.impl.transporter.api;

import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.transporter.impl.pattern.QuadConsumer;
import dev.lazurite.rayon.impl.transporter.impl.PatternBufferImpl;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Random;

@Environment(EnvType.CLIENT)
public interface Disassembler {
    static Pattern patternFrom(Item item) {
        QuadConsumer pattern = new QuadConsumer();
        MinecraftClient.getInstance().getItemRenderer()
                .renderItem(new ItemStack(item), ModelTransformation.Mode.GROUND, 0, 0, new MatrixStack(), pattern.asProvider());
        PatternBufferImpl.getInstance().put(Registry.ITEM.getId(item), pattern);
        return pattern;
    }

    static QuadConsumer patternFrom(BlockState blockState, BlockPos pos, World world) {
        QuadConsumer pattern = new QuadConsumer();
        MatrixStack matrixStack = new MatrixStack();

        matrixStack.translate(-0.5, -0.5, -0.5);
        MinecraftClient.getInstance().getBlockRenderManager()
                .renderBlock(blockState, pos, world, matrixStack, pattern, false, new Random());
        PatternBufferImpl.getInstance().put(Registry.BLOCK.getId(blockState.getBlock()), pattern);

        return pattern;
    }

    static QuadConsumer patternFrom(Entity entity) {
        QuadConsumer pattern = new QuadConsumer();
        MatrixStack matrixStack = new MatrixStack();
        Box box = entity.getBoundingBox();

//        matrixStack.translate(-box.getXLength() / 2.0, -box.getYLength() / 2.0, -box.getZLength() / 2.0);
        MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity)
                .render(entity, 0, 0, matrixStack, pattern.asProvider(), 0);
        PatternBufferImpl.getInstance().put(Registry.ENTITY_TYPE.getId(entity.getType()), pattern);

        return pattern;
    }
}
