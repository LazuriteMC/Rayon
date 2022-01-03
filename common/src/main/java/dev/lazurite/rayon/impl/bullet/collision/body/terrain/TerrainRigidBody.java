package dev.lazurite.rayon.impl.bullet.collision.body.terrain;

import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.cache.ChunkCache;
import dev.lazurite.rayon.impl.util.debug.Debuggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TerrainRigidBody extends PhysicsRigidBody implements Debuggable {
    private final BlockPos blockPos;
    private final BlockState state;

    public static TerrainRigidBody from(ChunkCache.BlockData blockData) {
        final var blockProperty = Rayon.getBlockProperty(blockData.blockState().getBlock());
        final var friction = blockProperty == null ? 0.75f : blockProperty.friction();
        final var restitution = blockProperty == null ? 0.25f : blockProperty.restitution();
        return new TerrainRigidBody(blockData.shape(), blockData.blockPos(), blockData.blockState(), friction, restitution);
    }

    public TerrainRigidBody(MinecraftShape shape, BlockPos blockPos, BlockState blockState, float friction, float restitution) {
        super(shape, PhysicsBody.massForStatic);
        this.blockPos = blockPos;
        this.state = blockState;

        this.setFriction(friction);
        this.setRestitution(restitution);
        this.setPhysicsLocation(new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f));
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public BlockState getBlockState() {
        return this.state;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TerrainRigidBody terrain) {
            return terrain.getBlockPos().equals(this.blockPos) && terrain.getBlockState().equals(this.state);
        }

        return false;
    }

    @Override
    public Vector3f getOutlineColor() {
        return new Vector3f(0.25f, 0.25f, 1.0f);
    }

    @Override
    public float getOutlineAlpha() {
        return 1.0f;
    }

    @Override
    public MinecraftShape getCollisionShape() {
        return (MinecraftShape) super.getCollisionShape();
    }
}