package dev.lazurite.rayon.impl.physics.world;

import com.google.common.collect.Lists;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.event.DynamicsWorldStepEvents;
import dev.lazurite.rayon.api.event.EntityRigidBodyEvents;
import dev.lazurite.rayon.impl.physics.body.BlockRigidBody;
import dev.lazurite.rayon.impl.physics.body.type.SteppableBody;
import dev.lazurite.rayon.impl.physics.helper.BlockHelper;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.thread.Clock;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.mixin.common.ServerWorldMixin;
import dev.lazurite.rayon.impl.mixin.client.MinecraftClientMixin;
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
public class MinecraftDynamicsWorld extends PhysicsSpace implements ComponentV3, PhysicsCollisionListener {
    private final BlockHelper blockHelper;
    private final Clock clock;
    private final World world;

    public MinecraftDynamicsWorld(World world, BroadphaseType broadphase) {
        super(broadphase);
        this.blockHelper = new BlockHelper(this);
        this.clock = new Clock();
        this.world = world;
        this.setGravity(new Vector3f(0, Config.getInstance().getGlobal().getGravity(), 0));
        this.addCollisionListener(this);
        DynamicsWorldStepEvents.WORLD_LOAD.invoker().onLoad(this);
    }

    public MinecraftDynamicsWorld(World world) {
        this(world, BroadphaseType.DBVT);
    }

    public void step(BooleanSupplier shouldStep) {
        if (shouldStep.getAsBoolean()) {
            /* Get delta time */
            float delta = this.clock.get();

            /* Run all start world step events */
            DynamicsWorldStepEvents.START_WORLD_STEP.invoker().onStartStep(this, delta);

            setGravity(new Vector3f(0, Config.getInstance().getGlobal().getGravity(), 0));
            blockHelper.load(getEntityRigidBodies());

            /* Step each EntityRigidBody */
            for (PhysicsRigidBody body : getRigidBodyList()) {
                if (body instanceof SteppableBody) {
                    ((EntityRigidBody) body).step(delta);
                }
            }

            /* Step the simulation */
            int maxSubSteps = Config.getInstance().getLocal().getMaxSubSteps();
            setAccuracy(delta / (float) maxSubSteps);
            update(delta, maxSubSteps);

            /* Run all end world step events */
            DynamicsWorldStepEvents.END_WORLD_STEP.invoker().onEndStep(this, delta);
        } else {
            this.clock.reset();
        }
    }

    public List<EntityRigidBody> getEntityRigidBodies() {
        List<EntityRigidBody> out = Lists.newArrayList();

        getRigidBodyList().forEach(body -> {
            if (body instanceof EntityRigidBody) {
                out.add((EntityRigidBody) body);
            }
        });

        return out;
    }

    public Clock getClock() {
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

    @Override
    public void addCollisionObject(PhysicsCollisionObject collisionObject) {
        if (collisionObject instanceof EntityRigidBody) {
            EntityRigidBodyEvents.ENTITY_BODY_LOAD.invoker().onLoad((EntityRigidBody) collisionObject, this);
        }

        super.addCollisionObject(collisionObject);
    }

    @Override
    public void removeCollisionObject(PhysicsCollisionObject collisionObject) {
        if (collisionObject instanceof EntityRigidBody) {
            EntityRigidBodyEvents.ENTITY_BODY_UNLOAD.invoker().onUnload((EntityRigidBody) collisionObject, this);
        }

        super.removeCollisionObject(collisionObject);
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        System.out.println("SCREAM");

        if (event.getObjectA() instanceof EntityRigidBody && event.getObjectA() instanceof EntityRigidBody) {
            EntityRigidBodyEvents.ENTITY_COLLISION.invoker().onEntityCollision((EntityRigidBody) event.getObjectA(), (EntityRigidBody) event.getObjectB());
        } else if (event.getObjectA() instanceof BlockRigidBody && event.getObjectB() instanceof EntityRigidBody) {
            EntityRigidBodyEvents.BLOCK_COLLISION.invoker().onBlockCollision((EntityRigidBody) event.getObjectB(), (BlockRigidBody) event.getObjectA());
        } else if (event.getObjectA() instanceof EntityRigidBody && event.getObjectB() instanceof BlockRigidBody) {
            EntityRigidBodyEvents.BLOCK_COLLISION.invoker().onBlockCollision((EntityRigidBody) event.getObjectA(), (BlockRigidBody) event.getObjectB());
        }
    }
}
