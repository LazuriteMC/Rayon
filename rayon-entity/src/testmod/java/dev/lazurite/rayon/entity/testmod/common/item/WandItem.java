package dev.lazurite.rayon.entity.testmod.common.item;

import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.testmod.common.entity.CubeEntity;
import dev.lazurite.rayon.entity.testmod.EntityTestMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/**
 * This is just meant as a test item that spawns a {@link CubeEntity}
 */
public class WandItem extends Item {
    public WandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        HitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);

        if (!world.isClient()) {
            CubeEntity entity = new CubeEntity(EntityTestMod.CUBE_ENTITY, world);
            entity.updatePosition(hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);
//            entity.updatePosition(user.getPos().x, user.getPos().y, user.getPos().z);
//            entity.getRigidBody().setLinearVelocity(VectorHelper.vec3dToVector3f(user.getPos().subtract(hitResult.getPos()).normalize()).multLocal(-2));

            /* Set the physics element to be prioritized if the player is sneaking while right clicking with the wand. */
            if (user.isSneaking()) {
                entity.getRigidBody().prioritize(user);
            }

            world.spawnEntity(entity);
            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.pass(itemStack);
    }
}
