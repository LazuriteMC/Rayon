package dev.lazurite.rayon.impl.transporter;

import dev.lazurite.rayon.impl.mixin.client.ItemRendererAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@Environment(EnvType.CLIENT)
public interface Disassembler {
    interface EntityPattern {
        static Pattern getPattern(Entity entity) {
            Pattern pattern = new Pattern(PatternType.ENTITY);
            MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity)
                    .render(entity, 0, 0, new MatrixStack(), pattern.asProvider(), 0);
            return pattern;
        }

        static Pattern getPattern(EntityModel<?> model) {
            Pattern pattern = new Pattern(PatternType.ENTITY);
            model.render(new MatrixStack(), pattern, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f);
            return pattern;
        }
    }

    interface ItemPattern {
        static Pattern getPattern(ItemStack itemStack) {
            Pattern pattern = new Pattern(PatternType.ITEM);
            MinecraftClient.getInstance().getItemRenderer()
                    .renderItem(itemStack, ModelTransformation.Mode.GROUND, 0, 0, new MatrixStack(), pattern.asProvider());
            return pattern;
        }

        static Pattern getPattern(BakedModel model) {
            Pattern pattern = new Pattern(PatternType.ITEM);
            ((ItemRendererAccess) MinecraftClient.getInstance().getItemRenderer())
                    .invokeRenderBakedItemModel(model, new ItemStack(Items.AIR), 0, 0, new MatrixStack(), pattern);

            return pattern;
        }
    }

    interface BlockPattern {
        static Pattern getPattern(BlockState blockState, World world) {
            Pattern pattern = new Pattern(PatternType.BLOCK);
            MinecraftClient.getInstance().getBlockRenderManager()
                    .renderBlock(blockState, new BlockPos(0, 0, 0), world, new MatrixStack(), pattern, false, new Random());
            return pattern;
        }
    }
}
