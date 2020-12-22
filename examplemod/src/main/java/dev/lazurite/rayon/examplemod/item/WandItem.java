package dev.lazurite.rayon.examplemod.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
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
            CowEntity cow = EntityType.COW.create(world);

            cow.updatePosition(hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);
//            PhysicsWorld.getInstance().track(cow);
            world.spawnEntity(cow);

            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.pass(itemStack);
    }
}
