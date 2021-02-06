package dev.lazurite.rayon.impl.physics.body;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.physics.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.impl.physics.body.shape.PatternShape;
import dev.lazurite.rayon.impl.physics.body.type.DebuggableBody;
import dev.lazurite.rayon.impl.physics.manager.BlockManager;
import dev.lazurite.rayon.impl.physics.manager.DebugManager;
import dev.lazurite.rayon.impl.transporter.api.Disassembler;
import dev.lazurite.rayon.impl.transporter.api.buffer.PatternBuffer;
import dev.lazurite.rayon.impl.transporter.api.pattern.TypedPattern;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

/**
 * A basic {@link PhysicsRigidBody} class representing a block.
 * @see BlockManager
 */
public class BlockRigidBody extends PhysicsRigidBody implements DebuggableBody {
    private final BlockState blockState;
    private final BlockPos blockPos;
    private final World world;

    public BlockRigidBody(BlockState blockState, BlockPos blockPos, World world, float friction, float restitution) {
        super(createShape(blockState, blockPos, world), PhysicsRigidBody.massForStatic);
        this.blockState = blockState;
        this.blockPos = blockPos;
        this.world = world;
        this.setFriction(friction);
        this.setRestitution(restitution);
        this.setPhysicsLocation(new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f));
    }

    public static CollisionShape createShape(BlockState blockState, BlockPos blockPos, World world) {
        if (!blockState.isFullCube(world, blockPos)) {
            for (TypedPattern<BlockPos> pattern : PatternBuffer.getBlockBuffer(world).getAll()) {
                if (pattern.getIdentifier().equals(blockPos)) {
                    return new PatternShape(pattern, false);
                }
            }
        }

        VoxelShape voxel = blockState.getCollisionShape(world, blockPos);
        if (!voxel.isEmpty()) {
            return new BoundingBoxShape(voxel.getBoundingBox());
        }

        return new BoundingBoxShape(new Box(-0.5, -0.5, -0.5, 1, 1, 1));
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public World getWorld() {
        return this.world;
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
    public boolean equals(Object obj) {
        if (obj instanceof BlockRigidBody) {
            return ((BlockRigidBody) obj).getBlockPos().equals(this.blockPos);
        }

        return false;
    }
}
