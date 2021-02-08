package dev.lazurite.rayon.impl.physics.world;

import com.google.common.collect.Lists;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.api.event.DynamicsWorldEvents;
import dev.lazurite.rayon.api.event.EntityRigidBodyEvents;
import dev.lazurite.rayon.impl.physics.body.BlockRigidBody;
import dev.lazurite.rayon.impl.physics.body.type.AirResistantBody;
import dev.lazurite.rayon.impl.physics.body.type.BlockLoadingBody;
import dev.lazurite.rayon.impl.physics.body.type.SteppableBody;
import dev.lazurite.rayon.impl.physics.manager.FluidManager;
import dev.lazurite.rayon.impl.physics.manager.TerrainManager;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.util.thread.Clock;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
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
 * The {@link MinecraftDynamicsWorld#step} method is called from a separate physics thread. There is one
 * physics thread per {@link World}. The rate at which it is stepped is locked to <b>60 steps/second.</b>
 * What this means is that every event in {@link EntityRigidBodyEvents} and {@link DynamicsWorldEvents} are
 * run on a separate thread from the client or the server thread as well as all other physics-related logic
 * that stems from the {@link MinecraftDynamicsWorld#step} method.
 *
 * Additionally, there are world step events that can be utilized in {@link DynamicsWorldEvents}.
 * @see EntityRigidBody
 */
public class MinecraftDynamicsWorld extends PhysicsSpace implements ComponentV3, PhysicsCollisionListener {
    private static final int MAX_PRESIM_STEPS = 10;
    private static final long STEP_SIZE = 20L;

    private final TerrainManager terrainManager;
    private final FluidManager fluidManager;
    private final Thread thread;
    private final World world;
    private final Clock clock;
    private boolean destroyed;
    private int presimSteps;
    private long nextStep;

    public MinecraftDynamicsWorld(Thread thread, World world, BroadphaseType broadphase) {
        super(broadphase);
        this.thread = thread;
        this.world = world;
        this.nextStep = Util.getMeasuringTimeMs() + STEP_SIZE;
        this.clock = new Clock();
        this.terrainManager = new TerrainManager(this);
        this.fluidManager = new FluidManager(this);
        this.setGravity(new Vector3f(0, Config.getInstance().getGlobal().getGravity(), 0));
        this.addCollisionListener(this);
    }

    public MinecraftDynamicsWorld(Thread thread, World world) {
        this(thread, world, BroadphaseType.DBVT);
    }

    /**
     * This method performs the following steps:
     * <ul>
     *     <li>Triggers all {@link DynamicsWorldEvents#START_STEP} events.</li>
     *     <li>Removes any distant {@link PhysicsRigidBody}s.</li>
     *     <li>Applies air resistance to all {@link AirResistantBody}s using {@link FluidManager}.</li>
     *     <li>Loads blocks into the simulation around {@link BlockLoadingBody}s using {@link TerrainManager}.</li>
     *     <li>Steps each {@link SteppableBody}.</li>
     *     <li>Sets gravity to the value stored in {@link Config}.</li>
     *     <li>Triggers all collision events.</li>
     *     <li>Steps the simulation using {@link PhysicsSpace#update(float, int)}.</li>
     *     <li>Triggers all {@link DynamicsWorldEvents#END_STEP} events.</li>
     * </ul>
     *
     * Additionally, none of the above steps execute when either the world is empty
     * (no {@link PhysicsRigidBody}s) or when the {@link BooleanSupplier} shouldStep
     * returns false.<br><br>
     *
     * @see DynamicsWorldEvents
     * @see EntityRigidBodyEvents
     * @see FluidManager
     * @see TerrainManager
     */
    public void step() {
        if (Util.getMeasuringTimeMs() > nextStep) {
            nextStep = Util.getMeasuringTimeMs() + STEP_SIZE;

            if (!isPaused() && (!isEmpty() || isInPresim())) {
                float delta = this.clock.get();

                /* World Step Event */
                DynamicsWorldEvents.START_STEP.invoker().onStartStep(this, delta);

                /* Remove far away entities */
                for (EntityRigidBody body : getRigidBodiesByClass(EntityRigidBody.class)) {
                    if (!isBodyNearPlayer(body)) {
                        removeCollisionObject(body);
                    }
                }

                /* Air Resistance */
                getFluidManager().doAirResistance(getRigidBodiesByClass(AirResistantBody.class));

                /* Terrain Loading */
                getTerrainManager().load(getRigidBodiesByClass(BlockLoadingBody.class));

                /* Stepping */
                getRigidBodiesByClass(SteppableBody.class).forEach(body -> body.step(delta));

                /* Gravity */
                setGravity(new Vector3f(0, Config.getInstance().getGlobal().getGravity(), 0));

                /* Collision Events */
                distributeEvents();

                /* Step Simulation */
                if (presimSteps > MAX_PRESIM_STEPS) {
                    update(delta);
                } else ++presimSteps;

                /* World Step Event */
                DynamicsWorldEvents.END_STEP.invoker().onEndStep(this, delta);
            } else {
                this.clock.reset();
            }
        }
    }

    public TerrainManager getTerrainManager() {
        return this.terrainManager;
    }

    public FluidManager getFluidManager() {
        return this.fluidManager;
    }

    public boolean isPaused() {
        return getWorld().isClient() && MinecraftClient.getInstance().isPaused();
    }

    public boolean isInPresim() {
        return presimSteps < MAX_PRESIM_STEPS;
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

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public void destroy() {
        this.destroyed = true;

        try {
            getThread().join();
        } catch (InterruptedException e) {
            Rayon.LOGGER.error("Error joining physics thread.");
            e.printStackTrace();
        }
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

    public Thread getThread() {
        return this.thread;
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

    /**
     * On top of adding the given collision object to the world, it also triggers
     * the LOAD event in {@link EntityRigidBodyEvents} if the given collision object
     * is a {@link EntityRigidBody}.
     * @see EntityRigidBodyEvents
     * @param collisionObject the collision object to add
     */
    @Override
    public void addCollisionObject(PhysicsCollisionObject collisionObject) {
        if (collisionObject instanceof EntityRigidBody) {
            if (!collisionObject.isInWorld() && isBodyNearPlayer((EntityRigidBody) collisionObject)) {
                ((EntityRigidBody) collisionObject).onLoad();
                EntityRigidBodyEvents.LOAD.invoker().onLoad((EntityRigidBody) collisionObject, this);
                super.addCollisionObject(collisionObject);
            }
        } else {
            super.addCollisionObject(collisionObject);
        }
    }

    /**
     * On top of removing the given collision object from the world, it also triggers
     * the UNLOAD event in {@link EntityRigidBodyEvents} if the given collision object
     * is a {@link EntityRigidBody}.
     * @see EntityRigidBodyEvents
     * @param collisionObject the collision object to remove
     */
    @Override
    public void removeCollisionObject(PhysicsCollisionObject collisionObject) {
        if (collisionObject instanceof EntityRigidBody) {
            EntityRigidBodyEvents.UNLOAD.invoker().onUnload((EntityRigidBody) collisionObject, this);
        }

        super.removeCollisionObject(collisionObject);
    }

    /**
     * Trigger all collision events (e.g. block/entity or entity/entity).
     * @see EntityRigidBodyEvents
     * @param event the event context
     */
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
