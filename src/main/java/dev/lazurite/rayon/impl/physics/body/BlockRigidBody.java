package dev.lazurite.rayon.impl.physics.body;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.impl.physics.helper.BlockHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * A basic {@link RigidBody} class representing a block.
 * @see BlockHelper
 */
public class BlockRigidBody extends RigidBody {
    private final BlockPos blockPos;
    private final BlockState blockState;

    public BlockRigidBody(RigidBodyConstructionInfo info, BlockPos blockPos, BlockState blockState) {
        super(info);
        this.blockPos = blockPos;
        this.blockState = blockState;
    }

    public static BlockRigidBody create(BlockPos blockPos, BlockState blockState, CollisionShape shape, float friction) {
        /* Set the position of the rigid body to the block's position */
        Vector3f position = new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(), position, 1.0f)));

        /* Set up the rigid body's construction info and initialization */
        RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(0, motionState, shape, new Vector3f(0, 0, 0));
        BlockRigidBody body = new BlockRigidBody(ci, blockPos, blockState);
        body.setFriction(friction);

        return body;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockRigidBody) {
            return ((BlockRigidBody) obj).getBlockPos().equals(this.blockPos);
        }

        return false;
    }
}
