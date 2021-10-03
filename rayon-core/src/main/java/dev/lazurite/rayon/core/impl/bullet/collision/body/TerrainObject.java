package dev.lazurite.rayon.core.impl.bullet.collision.body;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.util.debug.Debuggable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.State;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Optional;

public class TerrainObject {
    private final PhysicsCollisionObject collisionObject;
    private final MinecraftSpace space;
    private final BlockPos blockPos;
    private final State state;

    public TerrainObject(MinecraftSpace space, BlockPos blockPos, FluidState fluidState) {
        this.space = space;
        this.blockPos = blockPos;
        this.state = fluidState;

        var voxelShape = fluidState.getShape(space.getWorld(), blockPos);
        var boundingBox = voxelShape.isEmpty() ? new Box(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f) : voxelShape.getBoundingBox();
        this.collisionObject = new Fluid(this, space, MinecraftShape.of(boundingBox));
    }

    public TerrainObject(MinecraftSpace space, BlockPos blockPos, BlockState blockState, float friction, float restitution) {
        this.space = space;
        this.blockPos = blockPos;
        this.state = blockState;

        var voxelShape = blockState.getCollisionShape(space.getWorld(), blockPos);
        var boundingBox = voxelShape.isEmpty() ? new Box(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f) : voxelShape.getBoundingBox();
        this.collisionObject = new Block(this, space, MinecraftShape.of(boundingBox), friction, restitution);
    }

    public PhysicsCollisionObject getCollisionObject() {
        return this.collisionObject;
    }

    public boolean isSolid() {
        return getBlockState().isPresent();
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public State getState() {
        return this.state;
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }

    public Optional<BlockState> getBlockState() {
        return Optional.ofNullable(this.state instanceof BlockState blockState ? blockState : null);
    }

    public Optional<FluidState> getFluidState() {
        return Optional.ofNullable(this.state instanceof FluidState fluidState ? fluidState : null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TerrainObject terrainObject) {
            return terrainObject.getBlockPos().equals(blockPos) && terrainObject.getState().equals(state);
        }

        return false;
    }

    // Marker interface
    public interface Terrain {
        TerrainObject getParent();
    }

    public static class Block extends PhysicsRigidBody implements Terrain, Debuggable {
        private final MinecraftSpace space;
        private final TerrainObject parent;

        public Block(TerrainObject parent, MinecraftSpace space, MinecraftShape shape, float friction, float restitution) {
            super(shape, 0.0f);
            this.parent = parent;
            this.space = space;
            this.setFriction(friction);
            this.setRestitution(restitution);

            var blockPos = parent.getBlockPos();
            this.setPhysicsLocation(new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f));
        }

        public MinecraftSpace getSpace() {
            return this.space;
        }

        @Override
        public TerrainObject getParent() {
            return this.parent;
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

    public static class Fluid extends PhysicsGhostObject implements Terrain, Debuggable {
        private final TerrainObject parent;
        private final MinecraftSpace space;

        public Fluid(TerrainObject parent, MinecraftSpace space, MinecraftShape shape) {
            super(shape);
            this.parent = parent;
            this.space = space;

            var blockPos = parent.getBlockPos();
            this.setPhysicsLocation(new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f));
        }

        public MinecraftSpace getSpace() {
            return this.space;
        }

        @Override
        public TerrainObject getParent() {
            return this.parent;
        }

        @Override
        public Vector3f getOutlineColor() {
            return new Vector3f(0.0f, 1.0f, 1.0f);
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
}
