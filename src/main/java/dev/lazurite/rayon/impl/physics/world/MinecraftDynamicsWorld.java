package dev.lazurite.rayon.impl.physics.world;

import com.google.common.collect.Lists;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.event.DynamicsWorldEvents;
import dev.lazurite.rayon.api.event.EntityRigidBodyEvents;
import dev.lazurite.rayon.impl.physics.body.BlockRigidBody;
import dev.lazurite.rayon.impl.physics.body.type.BlockLoadingBody;
import dev.lazurite.rayon.impl.physics.body.type.SteppableBody;
import dev.lazurite.rayon.impl.physics.manager.BlockManager;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.util.thread.Clock;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.mixin.common.ServerWorldMixin;
import dev.lazurite.rayon.impl.mixin.client.MinecraftClientMixin;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3d;
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
 * Additionally, there are world step events that can be utilized in {@link DynamicsWorldEvents}.
 * @see EntityRigidBody
 * @see ServerWorldMixin
 * @see MinecraftClientMixin
 */
public class MinecraftDynamicsWorld extends PhysicsSpace implements ComponentV3, PhysicsCollisionListener {
    private final BlockManager blockManager;
    private final Clock clock;
    private final World world;

    public MinecraftDynamicsWorld(World world, BroadphaseType broadphase) {
        super(broadphase);
        this.world = world;
        this.clock = new Clock();
        this.blockManager = new BlockManager(this);
        this.setGravity(new Vector3f(0, Config.getInstance().getGlobal().getGravity(), 0));
        this.addCollisionListener(this);
        DynamicsWorldEvents.WORLD_LOAD.invoker().onLoad(this);
    }

    public MinecraftDynamicsWorld(World world) {
        this(world, BroadphaseType.DBVT);
    }

    /**
     * This method performs the following steps:
     * <ul>
     *     <li>Triggers all {@link DynamicsWorldEvents#START_WORLD_STEP} events.</li>
     *     <li>Removes any distant {@link PhysicsRigidBody}s.</li>
     *     <li>Loads blocks into the simulation using {@link BlockManager}.</li>
     *     <li>Steps each {@link EntityRigidBody}s in the world.</li>
     *     <li>Sets gravity to the value stored in {@link Config}.</li>
     *     <li>Triggers all collision events.</li>
     *     <li>Steps the simulation using {@link PhysicsSpace#update(float, int)}.</li>
     *     <li>Triggers all {@link DynamicsWorldEvents#END_WORLD_STEP} events.</li>
     * </ul>
     *
     * Additionally, none of the above steps execute when either the world is empty
     * (no {@link PhysicsRigidBody}s) or when the {@link BooleanSupplier} shouldStep
     * returns false.<br><br>
     *
     * @see DynamicsWorldEvents
     * @see EntityRigidBodyEvents
     * @param shouldStep whether or not the simulation should step
     */
    public void step(BooleanSupplier shouldStep) {
        if (shouldStep.getAsBoolean() && !isEmpty()) {
            float delta = this.clock.get();
            DynamicsWorldEvents.START_WORLD_STEP.invoker().onStartStep(this, delta);

            for (PhysicsRigidBody body : getRigidBodyList()) {
                if (!isBodyNearPlayer(body) && body.isInWorld()) {
                    removeCollisionObject(body);
                }
            }

            getBlockManager().load(getRigidBodiesByClass(BlockLoadingBody.class));
            getRigidBodiesByClass(SteppableBody.class).forEach(body -> body.step(delta));
            setGravity(new Vector3f(0, Config.getInstance().getGlobal().getGravity(), 0));
            distributeEvents();

            update(delta, Config.getInstance().getLocal().getMaxSubSteps());
            DynamicsWorldEvents.END_WORLD_STEP.invoker().onEndStep(this, delta);
        } else {
            this.clock.reset();
        }
    }

    public BlockManager getBlockManager() {
        return this.blockManager;
    }

    public <T> List<T> getRigidBodiesByClass(Class<T> type) {
        List<T> out = Lists.newArrayList();

        getRigidBodyList().forEach(body -> {
            if (type.isAssignableFrom(body.getClass())) {
                out.add(type.cast(body));
            }
        });

        return out;
    }

    public boolean isBodyNearPlayer(PhysicsRigidBody body) {
        Vec3d pos = VectorHelper.vector3fToVec3d(body.getPhysicsLocation(new Vector3f()));
        int loadDistance = (Config.getInstance().getLocal().getLoadDistance() / 10) * 16;

        for (PlayerEntity player : getWorld().getPlayers()) {
            if (player.getPos().distanceTo(pos) < loadDistance) {
                return true;
            }
        }

        return false;
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
            if (!collisionObject.isInWorld() && isBodyNearPlayer((EntityRigidBody) collisionObject)) {
                ((EntityRigidBody) collisionObject).onLoad(this);
                super.addCollisionObject(collisionObject);
            }
        } else {
            super.addCollisionObject(collisionObject);
        }
    }

    @Override
    public void removeCollisionObject(PhysicsCollisionObject collisionObject) {
        if (collisionObject instanceof EntityRigidBody) {
            ((EntityRigidBody) collisionObject).onUnload(this);
        }

        super.removeCollisionObject(collisionObject);
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (event.getObjectA() instanceof EntityRigidBody && event.getObjectB() instanceof EntityRigidBody) {
            EntityRigidBodyEvents.ENTITY_COLLISION.invoker().onEntityCollision((EntityRigidBody) event.getObjectA(), (EntityRigidBody) event.getObjectB());
        } else if (event.getObjectA() instanceof BlockRigidBody && event.getObjectB() instanceof EntityRigidBody) {
            EntityRigidBodyEvents.BLOCK_COLLISION.invoker().onBlockCollision((EntityRigidBody) event.getObjectB(), (BlockRigidBody) event.getObjectA());
        } else if (event.getObjectA() instanceof EntityRigidBody && event.getObjectB() instanceof BlockRigidBody) {
            EntityRigidBodyEvents.BLOCK_COLLISION.invoker().onBlockCollision((EntityRigidBody) event.getObjectA(), (BlockRigidBody) event.getObjectB());
        }
    }
}
