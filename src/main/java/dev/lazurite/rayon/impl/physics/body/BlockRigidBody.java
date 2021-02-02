package dev.lazurite.rayon.impl.physics.body;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.physics.body.type.DebuggableBody;
import dev.lazurite.rayon.impl.physics.body.type.IdentifierBody;
import dev.lazurite.rayon.impl.physics.manager.BlockManager;
import dev.lazurite.rayon.impl.physics.manager.DebugManager;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

/**
 * A basic {@link PhysicsRigidBody} class representing a block.
 * @see BlockManager
 */
public class BlockRigidBody extends PhysicsRigidBody implements IdentifierBody, DebuggableBody {
    private final BlockPos blockPos;
    private final BlockState blockState;

    public BlockRigidBody(BlockPos blockPos, BlockState blockState, CollisionShape shape, float friction, float restitution) {
        super(shape, PhysicsRigidBody.massForStatic);
        this.blockPos = blockPos;
        this.blockState = blockState;
        this.setPhysicsLocation(new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f));
        this.setFriction(friction);
        this.setRestitution(restitution);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Override
    public Vector3f getOutlineColor() {
        return new Vector3f(1, 0, 1);
    }

    @Override
    public DebugManager.DebugLayer getDebugLayer() {
        return DebugManager.DebugLayer.BLOCK;
    }

    @Override
    public Identifier getIdentifier() {
        return Registry.BLOCK.getId(blockState.getBlock());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockRigidBody) {
            return ((BlockRigidBody) obj).getBlockPos().equals(this.blockPos);
        }

        return false;
    }
}
