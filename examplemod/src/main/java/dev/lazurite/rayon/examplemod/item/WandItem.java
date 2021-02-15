package dev.lazurite.rayon.examplemod.item;

import dev.lazurite.rayon.examplemod.ExampleMod;
import dev.lazurite.rayon.examplemod.entity.LivingCubeEntity;
import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/**
 * This is just meant as a test item that spawns a {@link RectangularPrismEntity}
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
//            RectangularPrismEntity entity = new RectangularPrismEntity(ExampleMod.RECTANGULAR_PRISM_ENTITY, world);
            LivingCubeEntity entity = new LivingCubeEntity(ExampleMod.LIVING_CUBE_ENTITY, world);
            entity.updatePosition(hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);
            entity.getRigidBody().prioritize(user);
            world.spawnEntity(entity);
            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.pass(itemStack);
    }
}
