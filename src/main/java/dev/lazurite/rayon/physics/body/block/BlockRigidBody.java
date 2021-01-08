package dev.lazurite.rayon.physics.body.block;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class BlockRigidBody extends RigidBody {
    private final BlockPos blockPos;

    public BlockRigidBody(RigidBodyConstructionInfo info, BlockPos blockPos) {
        super(info);
        this.blockPos = blockPos;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public static BlockRigidBody create(BlockPos blockPos, CollisionShape shape, float friction) {
        /* Set the position of the rigid body to the block's position */
        Vector3f position = new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(), position, 1.0f)));

        /* Set up the rigid body's construction info and initialization */
        RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(0, motionState, shape, new Vector3f(0, 0, 0));
        BlockRigidBody body = new BlockRigidBody(ci, blockPos);
        body.setFriction(friction);

        return body;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockRigidBody) {
            return ((BlockRigidBody) obj).getBlockPos().equals(this.blockPos);
        }

        return false;
    }
}
