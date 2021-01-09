package dev.lazurite.rayon.physics.world;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.google.common.collect.Lists;
import dev.lazurite.rayon.api.event.DynamicsWorldStepEvents;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.body.block.BlockRigidBody;
import dev.lazurite.rayon.physics.helper.BlockHelper;
import dev.lazurite.rayon.physics.body.SteppableBody;
import dev.lazurite.rayon.physics.body.entity.EntityRigidBody;
import dev.lazurite.rayon.util.config.Config;
import dev.lazurite.rayon.util.thread.Delta;
import dev.lazurite.rayon.mixin.common.world.ServerWorldMixin;
import dev.lazurite.rayon.mixin.client.MinecraftClientMixin;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Like {@link EntityRigidBody}, this is another integral class to Rayon. It is a {@link ComponentV3}
 * which is attached to every {@link World} object in Minecraft. Therefore, every Minecraft
 * world/dimension is capable of simulation rigid body dynamics.<br><br>
 *
 * World-level values are controlled here such as gravity and air density. This class also facilitates
 * the loading of blocks into the world as {@link BlockRigidBody} objects.<br><br>
 *
 * The {@link MinecraftDynamicsWorld#step} method is called in two separate mixins: {@link MinecraftClientMixin}
 * and {@link ServerWorldMixin}. {@link MinecraftClientMixin} allows for running the simulation at any rate
 * from 20 steps/second to the frame rate of the game. {@link ServerWorldMixin}, however, is only capable of
 * stepping at 20 steps/second on the server.<br><br>
 *
 * Additionally, there are world step events that can be utilized in {@link DynamicsWorldStepEvents}.
 * @see EntityRigidBody
 * @see ServerWorldMixin
 * @see MinecraftClientMixin
 */
public class MinecraftDynamicsWorld extends DebuggableDynamicsWorld implements ComponentV3 {
    private final BlockHelper blockHelper;
    private final Delta clock;
    private final World world;

    private MinecraftDynamicsWorld(World world, Dispatcher dispatcher, BroadphaseInterface broadphase, ConstraintSolver constraintSolver, CollisionConfiguration collisionConfiguration) {
        super(dispatcher, broadphase, constraintSolver, collisionConfiguration);
        this.blockHelper = new BlockHelper(this);
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
            /* Run all start world step events */
            DynamicsWorldStepEvents.START_WORLD_STEP.invoker().onStartStep(this);

            float delta = this.clock.get();
            setGravity(new Vector3f(0, Config.INSTANCE.gravity, 0));
            blockHelper.load(getDynamicEntities(), new Box(new BlockPos(0, 0, 0)).expand(Config.INSTANCE.blockDistance));

            /* Step each SteppableBody object */
            getCollisionObjectArray().forEach(body -> {
                if (body instanceof SteppableBody) {
                    ((SteppableBody) body).step(delta);
                }
            });

            /* Step the DiscreteDynamicsWorld simulation */
            stepSimulation(delta, 5, delta / 5.0f);

            /* Run all end world step events */
            DynamicsWorldStepEvents.END_WORLD_STEP.invoker().onEndStep(this);
        } else {
            this.clock.reset();
        }
    }

    public List<EntityRigidBody> getDynamicEntities() {
        List<EntityRigidBody> out = Lists.newArrayList();

        getCollisionObjectArray().forEach(body -> {
            if (body instanceof EntityRigidBody) {
                out.add((EntityRigidBody) body);
            }
        });

        return out;
    }

    public Delta getClock() {
        return this.clock;
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

    public List<RigidBody> getTouching(EntityRigidBody dynamicEntity) {
        List<RigidBody> bodies = Lists.newArrayList();

        for (int manifoldNum = 0; manifoldNum < getDispatcher().getNumManifolds(); ++manifoldNum) {
            PersistentManifold manifold = getDispatcher().getManifoldByIndexInternal(manifoldNum);

            /* If both rigid bodies are blocks */
            if (manifold.getBody0() instanceof BlockRigidBody && manifold.getBody1() instanceof BlockRigidBody) {
                continue;
            }

            for (int contactNum = 0; contactNum < manifold.getNumContacts(); ++contactNum) {
                if (manifold.getContactPoint(contactNum).getDistance() <= 0.0f) {
                    if (dynamicEntity.equals(manifold.getBody0()) && !bodies.contains((RigidBody) manifold.getBody1())) {
                        bodies.add((RigidBody) manifold.getBody1());
                    } else if (dynamicEntity.equals(manifold.getBody1()) && !bodies.contains((RigidBody) manifold.getBody0())) {
                        bodies.add((RigidBody) manifold.getBody0());
                    }
                }
            }
        }

        return bodies;
    }
}
