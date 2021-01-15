package dev.lazurite.rayon.physics.world;

import com.google.common.collect.Lists;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.event.DynamicsWorldStepEvents;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.body.BlockRigidBody;
import dev.lazurite.rayon.physics.helper.BlockHelper;
import dev.lazurite.rayon.physics.body.SteppableBody;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.util.Clock;
import dev.lazurite.rayon.util.config.Config;
import dev.lazurite.rayon.mixin.common.world.ServerWorldMixin;
import dev.lazurite.rayon.mixin.client.MinecraftClientMixin;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

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
public class MinecraftDynamicsWorld extends PhysicsSpace implements ComponentV3 {
    private final BlockHelper blockHelper;
    private final Clock clock;
    private final World world;

    public MinecraftDynamicsWorld(World world, BroadphaseType broadphase) {
        super(broadphase);
        this.blockHelper = new BlockHelper(this);
        this.clock = new Clock();
        this.world = world;
        setGravity(new Vector3f(0, Config.INSTANCE.getGlobal().getGravity(), 0));
    }

    public MinecraftDynamicsWorld(World world) {
        this(world, BroadphaseType.DBVT);
    }

    public static MinecraftDynamicsWorld get(World world) {
        return Rayon.DYNAMICS_WORLD.get(world);
    }

    public void step(BooleanSupplier shouldStep) {
        if (shouldStep.getAsBoolean()) {
            /* Get delta time */
            float delta = this.clock.get();

            /* Run all start world step events */
            DynamicsWorldStepEvents.START_WORLD_STEP.invoker().onStartStep(this, delta);

            setGravity(new Vector3f(0, Config.INSTANCE.getGlobal().getGravity(), 0));
            blockHelper.load(getDynamicEntities());

            // TODO might cause bugs
            /* Step each SteppableBody object */
            for (PhysicsRigidBody body : getRigidBodyList()) {
                if (body instanceof SteppableBody) {
                    ((SteppableBody) body).step(delta);
                }
            }

            /* Step the DiscreteDynamicsWorld simulation */
//            stepSimulation(delta, 5, delta / 5.0f);
            update(5);

            /* Run all end world step events */
            DynamicsWorldStepEvents.END_WORLD_STEP.invoker().onEndStep(this, delta);
        } else {
            this.clock.reset();
        }
    }

    public List<EntityRigidBody> getDynamicEntities() {
        List<EntityRigidBody> out = Lists.newArrayList();

        getRigidBodyList().forEach(body -> {
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

    public List<PhysicsRigidBody> getTouching(EntityRigidBody dynamicEntity) {
        List<PhysicsRigidBody> bodies = Lists.newArrayList();

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
