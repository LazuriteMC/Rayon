package dev.lazurite.rayon.examplemod.item;

import dev.lazurite.rayon.examplemod.ExampleMod;
import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import dev.lazurite.rayon.physics.rigidbody.entity.DynamicBodyEntity;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/**
 * This is just meant as a test item for physics entities.
 * @author Ethan Johnson
 */
public class WandItem extends Item {
    /**
     * The default constructor.
     * @param settings the item settings
     */
    public WandItem(Settings settings) {
        super(settings);
    }

    /**
     * This method handles whenever a player right-clicks while holding this item.
     * @param world the world the item is in
     * @param user the player who right-clicked
     * @param hand the hand of the player who right-clicked
     * @return the action result (success, fail, etc.)
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        HitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);

        if (!world.isClient()) {
            RectangularPrismEntity rectangularPrism = new RectangularPrismEntity(ExampleMod.RECTANGULAR_PRISM_ENTITY, world);
            DynamicBodyEntity.get(rectangularPrism).setPosition(VectorHelper.vec3dToVector3f(hitResult.getPos()));
            world.spawnEntity(rectangularPrism);

            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.pass(itemStack);
    }
}
