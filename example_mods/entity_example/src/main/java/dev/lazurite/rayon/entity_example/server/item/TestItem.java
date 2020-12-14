package dev.lazurite.rayon.entity_example.server.item;

import dev.lazurite.rayon.entity_example.server.ServerInitializer;
import dev.lazurite.rayon.entity_example.server.entity.TestEntity;
import dev.lazurite.rayon.entity.PhysicsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;

public class TestItem extends Item {

    public TestItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        HitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);

        if (!world.isClient()) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                TestEntity testEntity = new TestEntity(ServerInitializer.TEST_ENTITY, world);

                Vector3f position = new Vector3f((float) hitResult.getPos().x, (float) hitResult.getPos().y, (float) hitResult.getPos().z);
                int id = user.getEntityId();
                float yaw = user.yaw;

                testEntity.setYaw(yaw);
                testEntity.getPhysics().setPosition(position);
                testEntity.setValue(PhysicsEntity.PLAYER_ID, id);

                world.spawnEntity(testEntity);

                itemStack.decrement(1);
                itemStack = new ItemStack(Items.AIR);

                return TypedActionResult.success(itemStack);
            }
        }

        return TypedActionResult.pass(itemStack);
    }
}
