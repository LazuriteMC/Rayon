package dev.lazurite.rayon.physics.world;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import dev.lazurite.rayon.physics.Rayon;
import dev.lazurite.rayon.physics.helper.BlockHelper;
import dev.lazurite.rayon.physics.helper.EntityHelper;
import dev.lazurite.rayon.physics.util.config.Config;
import dev.lazurite.rayon.physics.util.thread.Delta;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;

public class MinecraftDynamicsWorld extends DebuggableDynamicsWorld implements ComponentV3 {
    private final BlockHelper blockHelper;
    private final EntityHelper entityHelper;
    private final Delta clock;
    private final World world;

    private MinecraftDynamicsWorld(World world, Dispatcher dispatcher, BroadphaseInterface broadphase, ConstraintSolver constraintSolver, CollisionConfiguration collisionConfiguration) {
        super(dispatcher, broadphase, constraintSolver, collisionConfiguration);

        setGravity(new Vector3f(0, Config.INSTANCE.gravity, 0));
        this.blockHelper = new BlockHelper(this);
        this.entityHelper = new EntityHelper(this);
        this.clock = new Delta();
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
        return Rayon.DYNAMICS_WORLD.get(world);
    }

    public void step() {
        float delta = this.clock.get();

        blockHelper.load(entityHelper);
        entityHelper.step(delta);
        stepSimulation(delta, 5, delta/5.0f);
    }

    public World getWorld() {
        return this.world;
    }

    public EntityHelper getEntityHelper() {
        return this.entityHelper;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {

    }

    @Override
    public void writeToNbt(CompoundTag tag) {

    }
}
