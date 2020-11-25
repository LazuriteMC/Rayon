package dev.lazurite.api.physics.client.helper;

import com.bulletphysics.collision.shapes.CollisionShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class ShapeHelper {
    public static CollisionShape getItemShape(ItemStack stack) {
        BakedModel model = client.getItemRenderer().getHeldItemModel(stack, client.world, null);
//        BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(stack);

        Vector3f extents = new Vector3f(0.25f, 0.25f, 1.0f / 16.0f);
        BoxShape box = new BoxShape(extents);
        box.setMargin(0.04f);

        return box;
    }

    public static CollisionShape getBlockShape(ItemStack stack) {
        Block block = Block.getBlockFromItem(stack.getItem());
        return getBlockShape(block, block.getDefaultState());
    }

    public static CollisionShape getBlockShape(Block block, BlockState state) {
        BakedModel model = client.getBlockRenderManager().getModels().getModel(state);
        return new BakedModelShape(model, state);
    }

    public static CollisionShape getEntityShape(Entity entity) {
        return null;
    }
}
