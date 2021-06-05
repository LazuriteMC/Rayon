package dev.lazurite.rayon.entity.testmod.common.item;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.testmod.common.entity.CubeEntity;
import dev.lazurite.rayon.entity.testmod.EntityTestMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Random;

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
            CubeEntity entity = new CubeEntity(EntityTestMod.SMOL_CUBE_ENTITY, world);
            entity.getRigidBody().prioritize(user);
            entity.getRigidBody().setMass(1.0f); // 0.0175

            if (user.isSneaking()) {
                Random random = new Random();
                Vec3d unit = hitResult.getPos().subtract(user.getPos()).normalize();
                entity.updatePosition(user.getPos().x + unit.x, user.getPos().y + user.getStandingEyeHeight(), user.getPos().z + unit.z);
                ((EntityPhysicsElement) entity).getRigidBody().setLinearVelocity(VectorHelper.vec3dToVector3f(unit).multLocal(10));
                ((EntityPhysicsElement) entity).getRigidBody().setAngularVelocity(new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()));
            } else {
                ((EntityPhysicsElement) entity).getRigidBody().setAngularVelocity(new Vector3f(0, 0.5f, 1.0f));
                entity.updatePosition(hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);
            }

            world.spawnEntity(entity);
            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.pass(itemStack);
    }
}
