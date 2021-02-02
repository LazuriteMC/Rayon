package dev.lazurite.rayon.impl.transporter.disassembler;

import dev.lazurite.rayon.impl.mixin.client.render.ItemRendererAccess;
import dev.lazurite.rayon.impl.transporter.Pattern;
import dev.lazurite.rayon.impl.transporter.PatternType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public interface ItemDisassembler {
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
