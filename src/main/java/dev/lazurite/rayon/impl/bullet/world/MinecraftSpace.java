package dev.lazurite.rayon.impl.bullet.world;

import com.google.common.collect.Lists;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.body.type.TerrainLoadingBody;
import dev.lazurite.rayon.impl.bullet.manager.FluidManager;
import dev.lazurite.rayon.impl.bullet.manager.TerrainManager;
import dev.lazurite.rayon.impl.bullet.thread.Clock;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.util.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BooleanSupplier;

public class MinecraftSpace extends PhysicsSpace implements PhysicsCollisionListener {
    private static final int MAX_PRESIM_STEPS = 30;

    private final TerrainManager terrainManager;
    private final FluidManager fluidManager;
    private final PhysicsThread thread;
    private final World world;
    private final Clock clock;
    private boolean destroyed;
    private int presimSteps;

    public MinecraftSpace(PhysicsThread thread, World world, BroadphaseType broadphase) {
        super(broadphase);
        this.thread = thread;
        this.world = world;
        this.clock = new Clock();
        this.terrainManager = new TerrainManager(this);
        this.fluidManager = new FluidManager();
        this.setGravity(new Vector3f(0, Config.getInstance().getGlobal().getGravity(), 0));
    }

    public MinecraftSpace(PhysicsThread thread, World world) {
        this(thread, world, BroadphaseType.DBVT);
    }

    /**
     * This method performs the following steps:
     * <ul>
     *     <li>Applies air resistance to all {@link ElementRigidBody}s using {@link FluidManager}.</li>
     *     <li>Loads blocks into the simulation around {@link ElementRigidBody}s using {@link TerrainManager}.</li>
     *     <li>Sets gravity to the value stored in {@link Config}.</li>
     *     <li>Triggers all collision events.</li>
     *     <li>Steps the simulation using {@link PhysicsSpace#update(float, int)}.</li>
     * </ul>
     *
     * Additionally, none of the above steps execute when either the world is empty
     * (no {@link PhysicsRigidBody}s) or when the {@link BooleanSupplier} shouldStep
     * returns false.<br><br>
     *
     * @see FluidManager
     * @see TerrainManager
     */
    public void step() {
        if (!isPaused() && (!isEmpty() || isInPresim())) {
            float delta = this.clock.get();

            /* Steppp */
            getRigidBodiesByClass(ElementRigidBody.class).forEach(body -> body.getElement().step(this));

            /* Air Resistance */
            getFluidManager().doAirResistance(getRigidBodyList());

            /* Terrain Loading */
            getTerrainManager().load(getRigidBodiesByClass(TerrainLoadingBody.class));

            /* Gravity */
            setGravity(new Vector3f(0, Config.getInstance().getGlobal().getGravity(), 0));

            /* Collision Events */
            distributeEvents();

            /* Step Simulation */
            if (presimSteps > MAX_PRESIM_STEPS) {
                update(delta);
            } else ++presimSteps;
        } else {
            this.clock.reset();
        }
    }

    public TerrainManager getTerrainManager() {
        return this.terrainManager;
    }

    public FluidManager getFluidManager() {
        return this.fluidManager;
    }

    public boolean isPaused() {
        return MinecraftClient.getInstance().isPaused();
    }

    public boolean isInPresim() {
        return presimSteps < MAX_PRESIM_STEPS;
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

    /**
     * Trigger all collision events (e.g. block/entity or entity/entity).
     * @param event the event context
     */
    @Override
    public void collision(PhysicsCollisionEvent event) {
//        if (event.getObjectA() instanceof SynchronousRigidBody && event.getObjectB() instanceof SynchronousRigidBody) {
//            EntityRigidBodyEvents.ENTITY_COLLISION.invoker().onEntityCollision((SynchronousRigidBody) event.getObjectA(), (SynchronousRigidBody) event.getObjectB());
//        } else if (event.getObjectA() instanceof BlockRigidBody && event.getObjectB() instanceof SynchronousRigidBody) {
//            EntityRigidBodyEvents.BLOCK_COLLISION.invoker().onBlockCollision((SynchronousRigidBody) event.getObjectB(), (BlockRigidBody) event.getObjectA());
//        } else if (event.getObjectA() instanceof SynchronousRigidBody && event.getObjectB() instanceof BlockRigidBody) {
//            EntityRigidBodyEvents.BLOCK_COLLISION.invoker().onBlockCollision((SynchronousRigidBody) event.getObjectA(), (BlockRigidBody) event.getObjectB());
//        }
    }
}
