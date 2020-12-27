package dev.lazurite.rayon.api.physics.world;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.google.common.collect.Lists;
import dev.lazurite.rayon.api.Rayon;
import dev.lazurite.rayon.api.physics.entity.PhysicsEntityComponent;
import dev.lazurite.rayon.api.physics.helper.BlockHelper;
import dev.lazurite.rayon.api.physics.util.Constants;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.List;

public class MinecraftDynamicsWorld extends DebuggableDynamicsWorld implements ComponentV3, AutoSyncedComponent {
    private final BlockHelper blockHelper;
    private final List<Entity> entities;
    private final World world;

    private MinecraftDynamicsWorld(World world, Dispatcher dispatcher, BroadphaseInterface broadphase, ConstraintSolver constraintSolver, CollisionConfiguration collisionConfiguration) {
        super(dispatcher, broadphase, constraintSolver, collisionConfiguration);

        setGravity(new Vector3f(0, Constants.GRAVITY, 0));
        this.blockHelper = new BlockHelper(this);
        this.entities = Lists.newArrayList();
        this.world = world;
    }

    public static MinecraftDynamicsWorld create(World world) {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        return new MinecraftDynamicsWorld(world, dispatcher, broadphase, solver, collisionConfiguration);
    }

    public static MinecraftDynamicsWorld get(World world) {
        return Rayon.PHYSICS_WORLD.get(world);
    }

    public void step(float delta) {
        blockHelper.load(getEntities(), world);

        entities.forEach(entity -> PhysicsEntityComponent.get(entity).step(delta));

        blockHelper.unload();
        stepSimulation(delta, 5, delta/5.0f);
    }

    public List<Entity> getEntities() {
        return Lists.newArrayList(entities);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {

    }

    @Override
    public void writeToNbt(CompoundTag tag) {

    }
}
