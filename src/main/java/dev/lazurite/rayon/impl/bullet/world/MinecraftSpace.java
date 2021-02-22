package dev.lazurite.rayon.impl.bullet.world;

import com.google.common.collect.Lists;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.api.event.ElementCollisionEvents;
import dev.lazurite.rayon.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.body.BlockRigidBody;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.body.type.AirDragBody;
import dev.lazurite.rayon.impl.bullet.body.type.TerrainLoadingBody;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.util.thread.Clock;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.util.thread.Pausable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.World;

import java.util.List;

/**
 * This is the physics simulation environment for all {@link BlockRigidBody}s and {@link ElementRigidBody}s. It runs
 * on a separate thread from the rest of the game using {@link PhysicsThread}. Users shouldn't have to interact with
 * this object too much.<br>
 * To gain access to the world's {@link MinecraftSpace}, you can either call {@link PhysicsThread#execute}, register
 * a step event in {@link PhysicsSpaceEvents}, or call {@link PhysicsThread#getSpace()}. As a rule of thumb, if you
 * need to modify information in the physics world (e.g. add a rigid body) then you should always perform operations
 * on the same thread. {@link PhysicsSpaceEvents} and {@link PhysicsThread#execute} both get you onto the physics thread
 * while {@link PhysicsThread#getSpace()} does not. If you only need to read information from the physics world,
 * {@link PhysicsThread#getSpace()} is ok to use but should be considered a last resort.
 * @see PhysicsThread
 * @see PhysicsSpaceEvents
 */
public class MinecraftSpace extends PhysicsSpace implements Pausable, PhysicsCollisionListener {
    private static final int MAX_PRESIM_STEPS = 30;

    private final TerrainManager terrainManager;
    private final ThreadExecutor<?> server;
    private final PhysicsThread thread;
    private final World world;
    private final Clock clock;
    private boolean destroyed;
    private int presimSteps;
    private int maxSubSteps = 5;

    public MinecraftSpace(PhysicsThread thread, World world, BroadphaseType broadphase) {
        super(broadphase);
        this.thread = thread;
        this.world = world;
        this.clock = new Clock();
        this.terrainManager = new TerrainManager(this);
        this.server = world.getServer();
        this.setGravity(new Vector3f(0, Config.getInstance().getGravity(), 0));
        this.addCollisionListener(this);
    }

    public MinecraftSpace(PhysicsThread thread, World world) {
        this(thread, world, BroadphaseType.DBVT);
    }

    /**
     * This method performs the following steps:
     * <ul>
     *     <li>Fires world step events in {@link PhysicsSpaceEvents}.</li>
     *     <li>Steps {@link ElementRigidBody}s.</li>
     *     <li>Applies air drag force to all {@link AirDragBody}s.</li>
     *     <li>Loads blocks into the simulation around {@link TerrainLoadingBody}s using {@link TerrainManager}.</li>
     *     <li>Sets gravity to the value stored in {@link Config}.</li>
     *     <li>Triggers all collision events (queues up tasks in server thread).</li>
     *     <li>Steps the simulation using {@link PhysicsSpace#update(float, int)}.</li>
     * </ul>
     *
     * Additionally, none of the above steps execute when either the world is empty
     * (no {@link PhysicsRigidBody}s) or when the game is paused.
     *
     * @see TerrainManager
     * @see PhysicsSpaceEvents
     */
    public void step() {
        if (!isPaused() && (!isEmpty() || isInPresim())) {
            float delta = this.clock.get();

            /* World Step Event */
            PhysicsSpaceEvents.STEP.invoker().onStep(this);

            /* Steppp */
            getRigidBodiesByClass(ElementRigidBody.class).forEach(body -> body.getElement().step(this));

            /* Air Resistance */
            getRigidBodiesByClass(AirDragBody.class).forEach(AirDragBody::applyAirDrag);

            /* Terrain Loading */
            getTerrainManager().load(getRigidBodiesByClass(TerrainLoadingBody.class));

            /* Gravity */
            setGravity(new Vector3f(0, Config.getInstance().getGravity(), 0));

            /* Collision Events */
            if (!getWorld().isClient()) {
                distributeEvents();
            }

            /* Step Simulation */
            if (presimSteps > MAX_PRESIM_STEPS) {
                update(delta, maxSubSteps);
            } else ++presimSteps;
        } else {
            this.clock.reset();
        }
    }

    public TerrainManager getTerrainManager() {
        return this.terrainManager;
    }

    public boolean isInPresim() {
        return presimSteps < MAX_PRESIM_STEPS;
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    /**
     * Ends the {@link PhysicsThread} loop and joins
     * it back into the main thread.
     */
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

        for (PhysicsRigidBody body : getRigidBodyList()) {
            if (type.isAssignableFrom(body.getClass())) {
                out.add(type.cast(body));
            }
        }

        return out;
    }

    public PhysicsThread getThread() {
        return this.thread;
    }

    public World getWorld() {
        return this.world;
    }

    public void setMaxSubSteps(int maxSubSteps) {
        if (this.maxSubSteps < maxSubSteps) {
            this.maxSubSteps = maxSubSteps;
        }
    }

    /**
     * Trigger all collision events (e.g. block/element or element/element).
     * Executed on the main thread rather than the physics thread.
     * @param event the event context
     */
    @Override
    public void collision(PhysicsCollisionEvent event) {
        server.execute(() -> {
            if (event.getObjectA() instanceof ElementRigidBody && event.getObjectB() instanceof ElementRigidBody) {
                PhysicsElement element1 = ((ElementRigidBody) event.getObjectA()).getElement();
                PhysicsElement element2 = ((ElementRigidBody) event.getObjectB()).getElement();
                ElementCollisionEvents.ELEMENT_COLLISION.invoker().onCollide(element1, element2);

            } else if (event.getObjectA() instanceof BlockRigidBody && event.getObjectB() instanceof ElementRigidBody) {
                BlockPos blockPos = ((BlockRigidBody) event.getObjectA()).getBlockPos();
                BlockState blockState = ((BlockRigidBody) event.getObjectA()).getBlockState();
                PhysicsElement element = ((ElementRigidBody) event.getObjectB()).getElement();
                ElementCollisionEvents.BLOCK_COLLISION.invoker().onCollide(element, blockPos, blockState);

            } else if (event.getObjectA() instanceof ElementRigidBody && event.getObjectB() instanceof BlockRigidBody) {
                BlockPos blockPos = ((BlockRigidBody) event.getObjectB()).getBlockPos();
                BlockState blockState = ((BlockRigidBody) event.getObjectB()).getBlockState();
                PhysicsElement element = ((ElementRigidBody) event.getObjectA()).getElement();
                ElementCollisionEvents.BLOCK_COLLISION.invoker().onCollide(element, blockPos, blockState);
            }
        });
    }
}
