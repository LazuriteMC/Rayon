package dev.lazurite.rayon.examplemod.item;

import dev.lazurite.rayon.examplemod.ExampleMod;
import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import dev.lazurite.rayon.physics.body.entity.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import javax.vecmath.Quat4f;

/**
 * This is just meant as a test item for physics entities.
 * @author Ethan Johnson
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
            RectangularPrismEntity rectangularPrism = new RectangularPrismEntity(ExampleMod.RECTANGULAR_PRISM_ENTITY, world);

            EntityRigidBody body = EntityRigidBody.get(rectangularPrism);
            body.setPosition(VectorHelper.vec3dToVector3f(hitResult.getPos().add(new Vec3d(0, 1, 0))));

            Vec3d direction = hitResult.getPos().normalize();
            body.setLinearVelocity(VectorHelper.vec3dToVector3f(direction.multiply(20)));

            world.spawnEntity(rectangularPrism);

            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.pass(itemStack);
    }
}
