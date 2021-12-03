package dev.lazurite.rayon.impl.bullet.collision.space;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.event.collision.ElementCollisionEvents;
import dev.lazurite.rayon.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.TerrainObject;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.TerrainGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * This is the main physics simulation used by Rayon. Each bullet simulation update
 * happens asynchronously while all of the setup, input, or otherwise user defined
 * behavior happens on the game logic thread.
 * <br><br>
 * It is also worth noting that another
 * simulation step will not be performed if the last step has taken longer than 50ms and is still executing upon the
 * next tick. This really only happens if you are dealing with an ungodly amount of rigid bodies or your computer is slo.
 * @see PhysicsThread
 * @see PhysicsSpaceEvents
 */
public class MinecraftSpace extends PhysicsSpace implements PhysicsCollisionListener {
    private static final int MAX_PRESIM_STEPS = 30; // half a second

    private final List<TerrainObject> terrainObjects;
    private final PhysicsThread thread;
    private final Level level;
    private int presimSteps;

    private volatile boolean stepping;

    /**
     * Allows users to retrieve the {@link MinecraftSpace} associated
     * with any given {@link Level} object (client or server).
     * @param level the level to get the physics space from
     * @return the {@link MinecraftSpace}
     */
    public static MinecraftSpace get(Level level) {
        return ((SpaceStorage) level).getSpace();
    }

    public static Optional<MinecraftSpace> getOptional(Level level) {
        return Optional.ofNullable(get(level));
    }

    public MinecraftSpace(PhysicsThread thread, Level level) {
        super(
//                new Vector3f(-Level.MAX_LEVEL_SIZE, Level.MIN_ENTITY_SPAWN_Y, -Level.MAX_LEVEL_SIZE),
//                new Vector3f(Level.MAX_LEVEL_SIZE, Level.MAX_ENTITY_SPAWN_Y, Level.MAX_LEVEL_SIZE),
//                BroadphaseType.AXIS_SWEEP_3_32
                BroadphaseType.DBVT
        );

        this.thread = thread;
        this.level = level;
        this.terrainObjects = new ArrayList<>();
        this.setGravity(new Vector3f(0, -9.807f, 0));
        this.addCollisionListener(this);
        this.setAccuracy(1f/60f);
//        this.setMaxSubSteps(10);
    }

    /**
     * This method performs the following steps:
     * <ul>
     *     <li>Fires world step events in {@link PhysicsSpaceEvents}.</li>
     *     <li>Steps {@link ElementRigidBody}s.</li>
     *     <li>Steps the simulation asynchronously.</li>
     *     <li>Triggers collision events.</li>
     * </ul>
     *
     * Additionally, none of the above steps execute when either the world is empty
     * (no {@link PhysicsRigidBody}s) or when the game is paused.
     *
     * @see TerrainGenerator
     * @see PhysicsSpaceEvents
     */
    public void step() {
        MinecraftSpace.get(level).getRigidBodiesByClass(ElementRigidBody.class).forEach(ElementRigidBody::updateFrame);

        if (!isStepping() && (isInPresim() || !isEmpty())) {
            this.stepping = true;

            // This can only be done on each tick
            TerrainGenerator.step(this);

            final var futures = new CompletableFuture[3];

            // Step 3 times per tick, re-evaluating forces each step
            for (int i = 0; i < 3; ++i) {
                // Hop threads...
                futures[i] = CompletableFuture.runAsync(() -> {
                    if (presimSteps > MAX_PRESIM_STEPS) {
                        /* Call collision events */
                        this.distributeEvents();

                        /* World Step Event */
                        PhysicsSpaceEvents.STEP.invoke(this);

                        /* Step the Simulation */
                        this.update(1/60f);
                    } else {
                        ++presimSteps;
                    }
                }, getWorkerThread());
            }

            CompletableFuture.allOf(futures).thenRun(() -> this.stepping = false);
        }
    }

    @Override
    public void addCollisionObject(PhysicsCollisionObject collisionObject) {
        if (!collisionObject.isInWorld()) {
            if (collisionObject instanceof ElementRigidBody rigidBody) {
                PhysicsSpaceEvents.ELEMENT_ADDED.invoke(this, rigidBody);

                if (!rigidBody.isInWorld()) {
                    rigidBody.activate();
                    rigidBody.getFrame().set(
                            rigidBody.getPhysicsLocation(new Vector3f()),
                            rigidBody.getPhysicsLocation(new Vector3f()),
                            rigidBody.getPhysicsRotation(new Quaternion()),
                            rigidBody.getPhysicsRotation(new Quaternion()));
                }
            }

            super.addCollisionObject(collisionObject);
        }
    }

    @Override
    public void removeCollisionObject(PhysicsCollisionObject collisionObject) {
        if (collisionObject.isInWorld()) {
            super.removeCollisionObject(collisionObject);

            if (collisionObject instanceof ElementRigidBody rigidBody) {
                PhysicsSpaceEvents.ELEMENT_REMOVED.invoke(this, rigidBody);
            }
        }
    }

    public void addTerrainObject(TerrainObject terrainObject) {
        if (!this.terrainObjects.contains(terrainObject)) {
            this.terrainObjects.add(terrainObject);
            this.addCollisionObject(terrainObject.getCollisionObject());
        }
    }

    public void removeTerrainObject(TerrainObject terrainObject) {
        this.terrainObjects.remove(terrainObject);
        this.removeCollisionObject(terrainObject.getCollisionObject());
    }

    public boolean isServer() {
        return getWorkerThread().getParentExecutor() instanceof MinecraftServer;
    }

    public boolean isStepping() {
        return this.stepping;
    }

    public boolean isInPresim() {
        return presimSteps < MAX_PRESIM_STEPS;
    }

    public List<TerrainObject> getTerrainObjects() {
        return new ArrayList<>(this.terrainObjects);
    }

    public Optional<TerrainObject> getTerrainObjectAt(BlockPos blockPos) {
        for (var terrainObject : getTerrainObjects()) {
            if (terrainObject.getBlockPos().equals(blockPos)) {
                return Optional.of(terrainObject);
            }
        }

        return Optional.empty();
    }

    public <T> List<T> getRigidBodiesByClass(Class<T> type) {
        var out = new ArrayList<T>();

        for (var body : getRigidBodyList()) {
            if (type.isAssignableFrom(body.getClass())) {
                out.add(type.cast(body));
            }
        }

        return out;
    }

    public PhysicsThread getWorkerThread() {
        return this.thread;
    }

    public Level getLevel() {
        return this.level;
    }

    /**
     * Trigger all collision events (e.g. block/element or element/element).
     * @param event the event context
     */
    @Override
    public void collision(PhysicsCollisionEvent event) {
        float impulse = event.getAppliedImpulse();

        /* Element on Element */
        if (event.getObjectA() instanceof ElementRigidBody rigidBodyA && event.getObjectB() instanceof ElementRigidBody rigidBodyB) {
            ElementCollisionEvents.ELEMENT_COLLISION.invoke(rigidBodyA.getElement(), rigidBodyB.getElement(), impulse);

        /* Terrain on Element */
        } else if (event.getObjectA() instanceof TerrainObject.Terrain terrain && event.getObjectB() instanceof ElementRigidBody rigidBody) {
            ElementCollisionEvents.TERRAIN_COLLISION.invoke(rigidBody.getElement(), terrain.getParent(), impulse);

        /* Element on Terrain */
        } else if (event.getObjectA() instanceof ElementRigidBody rigidBody && event.getObjectB() instanceof TerrainObject.Terrain terrain) {
            ElementCollisionEvents.TERRAIN_COLLISION.invoke(rigidBody.getElement(), terrain.getParent(), impulse);
        }
    }
}