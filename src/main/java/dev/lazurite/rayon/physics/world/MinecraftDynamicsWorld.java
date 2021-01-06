package dev.lazurite.rayon.physics.world;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.google.common.collect.Lists;
import dev.lazurite.rayon.physics.Rayon;
import dev.lazurite.rayon.physics.helper.BlockHelper;
import dev.lazurite.rayon.physics.helper.EntityHelper;
import dev.lazurite.rayon.physics.body.SteppableBody;
import dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity;
import dev.lazurite.rayon.physics.util.config.Config;
import dev.lazurite.rayon.physics.util.thread.Delta;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.List;
import java.util.function.BooleanSupplier;

public class MinecraftDynamicsWorld extends DebuggableDynamicsWorld implements ComponentV3 {
    private final BlockHelper blockHelper;
    private final EntityHelper entityHelper;
    private final Delta clock;
    private final World world;

    private MinecraftDynamicsWorld(World world, Dispatcher dispatcher, BroadphaseInterface broadphase, ConstraintSolver constraintSolver, CollisionConfiguration collisionConfiguration) {
        super(dispatcher, broadphase, constraintSolver, collisionConfiguration);
        this.blockHelper = new BlockHelper(this);
        this.entityHelper = new EntityHelper(this);
        this.clock = new Delta();
        this.world = world;

        setGravity(new Vector3f(0, Config.INSTANCE.gravity, 0));
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

    public void step(BooleanSupplier shouldStep) {
        if (shouldStep.getAsBoolean()) {
            float delta = this.clock.get();
            setGravity(new Vector3f(0, Config.INSTANCE.gravity, 0));

            blockHelper.load(getDynamicEntities(), new Box(new BlockPos(0, 0, 0)).expand(Config.INSTANCE.blockDistance));
//            entityHelper.load(getDynamicEntities(), new Box(new BlockPos(0, 0, 0)).expand(Config.INSTANCE.entityDistance));

            getCollisionObjectArray().forEach(body -> {
                if (body instanceof SteppableBody) {
                    ((SteppableBody) body).step(delta);
                }
            });

            stepSimulation(delta, 5, delta / 5.0f);
        } else {
            this.clock.reset();
        }
    }

    public List<DynamicBodyEntity> getDynamicEntities() {
        List<DynamicBodyEntity> out = Lists.newArrayList();

        getCollisionObjectArray().forEach(body -> {
            if (body instanceof DynamicBodyEntity) {
                out.add((DynamicBodyEntity) body);
            }
        });

        return out;
    }

    public World getWorld() {
        return this.world;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {

    }

    @Override
    public void writeToNbt(CompoundTag tag) {

    }
}
