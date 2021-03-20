package dev.lazurite.rayon.core.impl.thread.space.body;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.thread.space.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.core.impl.thread.space.body.type.DebuggableBody;
import dev.lazurite.rayon.core.impl.thread.space.util.TerrainManager;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

/**
 * A basic {@link PhysicsRigidBody} class representing a piece of the terrain.
 * @see TerrainManager
 */
public class BlockRigidBody extends PhysicsRigidBody implements DebuggableBody {
    private final BlockState blockState;
    private final BlockPos blockPos;

    public BlockRigidBody(BlockState blockState, BlockPos blockPos, @Nullable CollisionShape shape, float friction, float restitution) {
        super(shape == null ? new BoundingBoxShape(new Box(-0.5, -0.5, -0.5, 1, 1, 1)) : shape, PhysicsRigidBody.massForStatic);
        this.blockState = blockState;
        this.blockPos = blockPos;
        this.setPhysicsLocation(new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f));
        this.setFriction(friction);
        this.setRestitution(restitution);
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    @Override
    public Vector3f getOutlineColor() {
        return new Vector3f(1, 0, 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockRigidBody) {
            return ((BlockRigidBody) obj).getBlockPos().equals(getBlockPos());
        }

        return false;
    }
}
