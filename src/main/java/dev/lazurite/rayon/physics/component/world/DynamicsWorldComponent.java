package dev.lazurite.rayon.physics.component.world;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import dev.lazurite.rayon.physics.component.SteppableComponent;
import dev.lazurite.rayon.physics.MinecraftDynamicsWorld;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

public class DynamicsWorldComponent implements ComponentV3, SteppableComponent, AutoSyncedComponent {
    private final MinecraftDynamicsWorld dynamicsWorld;
    private final World world;

    public DynamicsWorldComponent(World world) {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        this.dynamicsWorld = new MinecraftDynamicsWorld(world, dispatcher, broadphase, solver, collisionConfiguration);

        this.world = world;
    }

    @Override
    public void step(float delta) {
        float maxSubSteps = 5.0f;
        this.dynamicsWorld.stepSimulation(delta, (int) maxSubSteps, delta/maxSubSteps);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {

    }

    @Override
    public void writeToNbt(CompoundTag tag) {

    }
}
