package dev.lazurite.rayon.entity.testmod.common.item;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.entity.testmod.common.entity.EntityTestEntities;
import dev.lazurite.rayon.entity.testmod.common.entity.StoneBlockEntity;
import dev.lazurite.rayon.entity.testmod.EntityTestMod;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * This is just meant as a test item that spawns a {@link StoneBlockEntity}
 */
public class WandItem extends Item {
    public WandItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        final var itemStack = user.getItemInHand(hand);
        final var hitResult = getPlayerPOVHitResult(level, user, ClipContext.Fluid.NONE);

        if (!level.isClientSide()) {
            final var entity = new StoneBlockEntity(EntityTestEntities.STONE_BLOCK_ENTITY.get(), level);
            final var rigidBody = entity.getRigidBody();
            rigidBody.setMass(20);

            if (user.isCrouching()) {
                final var random = new Random();
                final var unit = hitResult.getLocation().subtract(user.position()).normalize();
                entity.absMoveTo(user.position().x + unit.x, user.position().y + user.getEyeHeight(), user.position().z + unit.z);
                rigidBody.setLinearVelocity(Convert.toBullet(unit).multLocal(20));
                rigidBody.setAngularVelocity(new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()));
            } else {
                entity.absMoveTo(hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z);
            }

            level.addFreshEntity(entity);
            return InteractionResultHolder.success(itemStack);
        }

        return InteractionResultHolder.pass(itemStack);
    }
}
