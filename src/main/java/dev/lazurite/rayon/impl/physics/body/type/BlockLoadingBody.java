package dev.lazurite.rayon.impl.physics.body.type;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.lazurite.rayon.impl.util.helper.BlockHelper;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Any {@link PhysicsRigidBody} with this assigned to it will load
 * blocks around it into the {@link MinecraftDynamicsWorld}.
 *
 * @see MinecraftDynamicsWorld
 * @see BlockHelper
 */
public interface BlockLoadingBody {
    MinecraftDynamicsWorld getDynamicsWorld();

    boolean isNoClipEnabled();

    BlockPos getBlockPos();
}
