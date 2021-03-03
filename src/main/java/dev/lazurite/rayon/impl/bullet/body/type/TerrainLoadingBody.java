package dev.lazurite.rayon.impl.bullet.body.type;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.lazurite.rayon.impl.bullet.body.BlockRigidBody;
import dev.lazurite.rayon.impl.bullet.world.TerrainManager;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import net.minecraft.util.math.BlockPos;

/**
 * Any {@link PhysicsRigidBody} with this interface assigned will load
 * {@link BlockRigidBody}s into the {@link MinecraftSpace} each step.
 * @see TerrainManager
 */
public interface TerrainLoadingBody {
    /**
     * @return the block position of the {@link PhysicsRigidBody}
     */
    BlockPos getBlockPos();

    /**
     * @return the max distance at which to load blocks in the {@link MinecraftSpace}
     */
    int getLoadDistance();

    /**
     * Whether or not the rigid body is in noclip mode.
     */
    boolean isInNoClip();
}
