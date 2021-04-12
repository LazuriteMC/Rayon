package dev.lazurite.rayon.core.impl.physics.space;

import com.google.common.collect.Lists;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.api.event.ElementCollisionEvents;
import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.RayonCoreCommon;
import dev.lazurite.rayon.core.impl.physics.space.body.BlockRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.type.TerrainLoading;
import dev.lazurite.rayon.core.impl.physics.space.environment.TerrainManager;
import dev.lazurite.rayon.core.impl.physics.space.util.SpaceStorage;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.util.math.BoxHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.core.impl.util.supplier.entity.EntitySupplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

/**
 * This is the physics simulation environment for all {@link BlockRigidBody}s and {@link ElementRigidBody}s. It runs
 * on a separate thread from the rest of the game using {@link PhysicsThread}. Users shouldn't have to interact with
 * this object too much.<br>
 * To gain access to the world's {@link MinecraftSpace}, you can call {@link MinecraftSpace#get(World)} or register
 * a step event in {@link PhysicsSpaceEvents}. As a rule of thumb, if you need to modify information in the physics
 * environment (e.g. add a rigid body or apply a force) then you should always perform those operations on the same
 * thread. The easiest way to get onto the physics thread is to queue a task using {@link PhysicsThread#execute(Runnable)}.
 * The {@link PhysicsThread} can be accessed using {@link MinecraftSpace#getThread()}. If you only need to read information
 * from the physics world, you don't need to queue a task.
 * @see PhysicsThread
 * @see PhysicsSpaceEvents
 */
public class MinecraftSpace extends PhysicsSpace implements PhysicsCollisionListener {
    public static final Identifier MAIN = new Identifier(RayonCoreCommon.MODID, "main");
    private static final int MAX_PRESIM_STEPS = 10;

    private final TerrainManager terrainManager;
    private final PhysicsThread thread;
    private final World world;
    private int presimSteps;
    private volatile boolean stepping;

    private float airDensity;
    private float waterDensity;
    private float lavaDensity;

    /**
     * Allows users to retrieve the {@link MinecraftSpace} associated
     * with any given {@link World} object (client or server).
     * @param world the world to get the physics space from
     * @return the {@link MinecraftSpace}
     */
    public static MinecraftSpace get(World world) {
        return ((SpaceStorage) world).getSpace(MAIN);
    }

    public MinecraftSpace(PhysicsThread thread, World world, BroadphaseType broadphase) {
        super(broadphase);
        this.thread = thread;
        this.world = world;
        this.terrainManager = new TerrainManager(this);
        this.addCollisionListener(this);

        this.setGravity(new Vector3f(0, -9.807f, 0)); // m/s/s
        this.setAirDensity(1.2f); // kg/m^3
        this.setWaterDensity(997f); // kg/m^3
        this.setLavaDensity(3100f); // kg/m^3

    }

    public MinecraftSpace(PhysicsThread thread, World world) {
        this(thread, world, BroadphaseType.DBVT);
    }

    /**
     * This method performs the following steps:
     * <ul>
     *     <li>Fires world step events in {@link PhysicsSpaceEvents}.</li>
     *     <li>Steps {@link ElementRigidBody}s.</li>
     *     <li>Applies air drag force to all {@link ElementRigidBody}s.</li>
     *     <li>Loads blocks into the simulation around {@link ElementRigidBody}s using {@link TerrainManager}.</li>
     *     <li>Triggers all collision events (queues up tasks in server thread).</li>
     *     <li>Steps the simulation using {@link PhysicsSpace#update(float, int)}.</li>
     * </ul>
     *
     * Additionally, none of the above steps execute when either the world is empty
     * (no {@link PhysicsRigidBody}s) or when the game is paused.
     *
     * @param shouldStep whether or not to fully step the simulation
     * @see TerrainManager
     * @see PhysicsSpaceEvents
     */
    public void step(BooleanSupplier shouldStep) {
        if (shouldStep.getAsBoolean()) {
            stepping = true;

            /* World Step Event */
            PhysicsSpaceEvents.STEP.invoker().onStep(this);

            getRigidBodiesByClass(ElementRigidBody.class).forEach(rigidBody -> {
                /* Frame Update */
                rigidBody.updateFrame();

                /* Entity Collisions */
                BoundingBox box = rigidBody.boundingBox(new BoundingBox());
                Vector3f location = rigidBody.getPhysicsLocation(new Vector3f()).subtract(new Vector3f(0, -box.getYExtent(), 0));
                float mass = rigidBody.getMass();

                EntitySupplier.getInsideOf(rigidBody).forEach(entity -> {
                    Vector3f entityPos = VectorHelper.vec3dToVector3f(entity.getPos().add(0, entity.getBoundingBox().getYLength(), 0));
                    Vector3f normal = location.subtract(entityPos).multLocal(new Vector3f(1, 0, 1)).normalize();

                    Box intersection = entity.getBoundingBox().intersection(BoxHelper.bulletToMinecraft(box));
                    Vector3f force = normal.clone().multLocal((float) intersection.getAverageSideLength() / (float) BoxHelper.bulletToMinecraft(box).getAverageSideLength())
                            .multLocal(mass).multLocal(new Vector3f(1, 0, 1));
                    rigidBody.applyCentralImpulse(force);
                });
            });

            getThread().execute(() -> {
                /* Step and Fluid Resistance */
                getRigidBodiesByClass(ElementRigidBody.class).forEach(rigidBody -> {
                    rigidBody.getElement().step(this);

                    if (rigidBody.shouldDoFluidResistance()) {
                        float dragCoefficient = rigidBody.getDragCoefficient();
                        float area = (float) Math.pow(rigidBody.boundingBox(new BoundingBox()).getExtent(new Vector3f()).lengthSquared(), 2);
                        float k = (getAirDensity() * dragCoefficient * area) / 2.0f;
                        Vector3f force = new Vector3f().set(rigidBody.getLinearVelocity(new Vector3f())).multLocal(-rigidBody.getLinearVelocity(new Vector3f()).lengthSquared()).multLocal(k);

                        if (Float.isFinite(force.lengthSquared()) && force.lengthSquared() > 0.1f) {
                            rigidBody.applyCentralForce(force);
                        }
                    }
                });

                getRigidBodiesByClass(TerrainLoading.class).forEach(terrainBody -> {
                    if (terrainBody.shouldDoTerrainLoading()) {
                        Vector3f pos = ((PhysicsRigidBody) terrainBody).getPhysicsLocation(new Vector3f());
                        Box box = new Box(new BlockPos(pos.x, pos.y, pos.z)).expand(terrainBody.getEnvironmentLoadDistance());
                        terrainManager.load(terrainBody, box);
                    }
                });

                terrainManager.purge();

                /* Step Simulation */
                if (presimSteps > MAX_PRESIM_STEPS) {
                    update(1 / 20f, 5);
                } else ++presimSteps;

                distributeEvents();
                stepping = false;
            });
        } else {
            // If we made it here, it means we're skipping steps due to poor performance.
            getRigidBodiesByClass(ElementRigidBody.class).forEach(ElementRigidBody::updateFrame);
        }
    }

    public void load(PhysicsElement element) {
        ElementRigidBody rigidBody = element.getRigidBody();

        if (!rigidBody.isInWorld()) {
            element.reset();
            addCollisionObject(rigidBody);
        }

        rigidBody.activate();
    }

    public void unload(PhysicsElement element) {
        if (element.getRigidBody().isInWorld()) {
            removeCollisionObject(element.getRigidBody());
        }
    }

    public boolean isServer() {
        return getThread().getParentExecutor() instanceof MinecraftServer;
    }

    public boolean isStepping() {
        return this.stepping;
    }

    public boolean canStep() {
        return !isStepping() && (isInPresim() || !isEmpty());
    }

    public boolean isInPresim() {
        return presimSteps < MAX_PRESIM_STEPS;
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

    public void setAirDensity(float airDensity) {
        this.airDensity = airDensity;
    }

    public void setWaterDensity(float waterDensity) {
        this.waterDensity = waterDensity;
    }

    public void setLavaDensity(float lavaDensity) {
        this.lavaDensity = lavaDensity;
    }

    public float getAirDensity() {
        return this.airDensity;
    }

    public float getWaterDensity() {
        return this.waterDensity;
    }

    public float getLavaDensity() {
        return this.lavaDensity;
    }

    public TerrainManager getTerrainManager() {
        return this.terrainManager;
    }

    /**
     * Trigger all collision events (e.g. block/element or element/element).
     * @param event the event context
     */
    @Override
    public void collision(PhysicsCollisionEvent event) {
        Executor thread = getThread().getParentExecutor();
        float impulse = event.getAppliedImpulse();

        /* Element on Element */
        if (event.getObjectA() instanceof ElementRigidBody && event.getObjectB() instanceof ElementRigidBody) {
            PhysicsElement element1 = ((ElementRigidBody) event.getObjectA()).getElement();
            PhysicsElement element2 = ((ElementRigidBody) event.getObjectB()).getElement();
            ElementCollisionEvents.ELEMENT_COLLISION.invoker().onCollide(thread, element1, element2, impulse);

        /* Block on Element */
        } else if (event.getObjectA() instanceof BlockRigidBody && event.getObjectB() instanceof ElementRigidBody) {
            BlockRigidBody block = (BlockRigidBody) event.getObjectA();
            PhysicsElement element = ((ElementRigidBody) event.getObjectB()).getElement();
            ElementCollisionEvents.BLOCK_COLLISION.invoker().onCollide(thread, element, block, impulse);

        /* Element on Block */
        } else if (event.getObjectA() instanceof ElementRigidBody && event.getObjectB() instanceof BlockRigidBody) {
            BlockRigidBody block = (BlockRigidBody) event.getObjectB();
            PhysicsElement element = ((ElementRigidBody) event.getObjectA()).getElement();
            ElementCollisionEvents.BLOCK_COLLISION.invoker().onCollide(thread, element, block, impulse);
        }
    }
}
