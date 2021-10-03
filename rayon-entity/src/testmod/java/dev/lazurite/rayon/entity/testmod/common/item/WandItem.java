package dev.lazurite.rayon.entity.testmod.common.item;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.entity.testmod.common.entity.StoneBlockEntity;
import dev.lazurite.rayon.entity.testmod.EntityTestMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Random;

/**
 * This is just meant as a test item that spawns a {@link StoneBlockEntity}
 */
public class WandItem extends Item {
    public WandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var itemStack = user.getStackInHand(hand);
        var hitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);

        if (!world.isClient()) {
            var entity = new StoneBlockEntity(EntityTestMod.STONE_BLOCK_ENTITY, world);
            var rigidBody = entity.getRigidBody();
            rigidBody.setMass(20);

            if (user.isSneaking()) {
                var random = new Random();
                var unit = hitResult.getPos().subtract(user.getPos()).normalize();
                entity.updatePosition(user.getPos().x + unit.x, user.getPos().y + user.getStandingEyeHeight(), user.getPos().z + unit.z);
                rigidBody.setLinearVelocity(Convert.toBullet(unit).multLocal(20));
                rigidBody.setAngularVelocity(new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()));
            } else {
                entity.updatePosition(hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);
            }

            world.spawnEntity(entity);
            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.pass(itemStack);
    }
}
