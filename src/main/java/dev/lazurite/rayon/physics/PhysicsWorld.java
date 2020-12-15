package dev.lazurite.rayon.physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.linearmath.Transform;
import com.google.common.collect.Lists;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.exception.PhysicsWorldTrackingException;
import dev.lazurite.rayon.physics.composition.PhysicsComposition;
import dev.lazurite.rayon.physics.helper.BlockCollisionHelper;
import dev.lazurite.rayon.render.DebugRenderer;
import dev.lazurite.rayon.physics.helper.VectorHelper;
import dev.lazurite.rayon.util.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main world class behind most of the Physics API. Every {@link RigidBody}, whether
 * it represents a block or an entity, ends up in here and is tracked within this class.
 * @author Ethan Johnson
 */
@Environment(EnvType.CLIENT)
public final class PhysicsWorld extends DiscreteDynamicsWorld {
    /** The list of {@link Entity} objects that are tracked by the {@link PhysicsWorld}. */
    private final List<Entity> entities;

    /** The {@link BlockCollisionHelper} responsible for loading/unloading blocks from the {@link PhysicsWorld}. */
    private final BlockCollisionHelper blockCollisionHelper;

    /** The renderer responsible for showing outlines of {@link RigidBody} objects in-game. */
    private final DebugRenderer debugRenderer;

    /** The {@link Clock} used for keeping time and calculating delta time in the main loop. */
    private final Clock clock;

    /** The instance variable used in place of instantiating {@link PhysicsWorld} yourself. */
    private static PhysicsWorld instance;

    /**
     * The constructor. Builds a {@link DiscreteDynamicsWorld} as well as
     * initializes lists and other class-level attributes.
     * @param dispatcher
     * @param broadphase
     * @param solver
     * @param collisionConfiguration
     */
    private PhysicsWorld(CollisionDispatcher dispatcher, BroadphaseInterface broadphase, SequentialImpulseConstraintSolver solver, CollisionConfiguration collisionConfiguration) {
        super(dispatcher, broadphase, solver, collisionConfiguration);

        this.entities = new ArrayList<>();
        this.blockCollisionHelper = new BlockCollisionHelper(this);
        this.debugRenderer = new DebugRenderer(this);
        this.clock = new Clock();

        this.setDebugDrawer(debugRenderer);
        this.setGravity(new Vector3f(0, Constants.GRAVITY, 0));
    }

    /**
     * Creates the {@link PhysicsWorld}. The reason this exists is
     * because there is more to set up than what can be done in the constructor.
     */
    public static void create() {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        PhysicsWorld.instance = new PhysicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
    }

    /**
     * @return the running instance of {@link PhysicsWorld}
     */
    public static PhysicsWorld getInstance() {
        return instance;
    }

    /**
     * This method gets called on the render thread every frame. It updates the
     * {@link PhysicsWorld} using delta time calculated from the {@link Clock} class.
     */
    public void stepWorld() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;

        List<Entity> toRemove = new ArrayList<>();
        float maxSubSteps = 5.0f;

        float delta = clock.getTimeMicroseconds() / 1000000F;
        clock.reset();

        this.entities.forEach(entity -> {
            if (!Rayon.hasPhysics(entity)) {
                toRemove.add(entity);
                return;
            }

            /* Get the Physics Composition object for the given entity. */
            PhysicsComposition physics = Rayon.getPhysics(entity);

            /* Build a list of entities to remove later-on. */
            if (entity.removed) {
                toRemove.add(entity);
                return;
            }

            if (world != null) {
                physics.step(entity, delta);

                /* Add the rigid body to the world if it isn't already there */
                if (!physics.getRigidBody().isInWorld()) {
                    this.addRigidBody(physics.getRigidBody());
                }

                /* Load in block collisions */
                if (!physics.getSynchronizer().get(PhysicsComposition.NO_CLIP)) {
                    this.blockCollisionHelper.load(entity, world);
                }
            }
        });

        /* Clean out unnecessary blocks. */
        this.blockCollisionHelper.unload();

        /* Clean out all "to remove" entities. */
        toRemove.forEach(entities::remove);

        /* Step the world using delta. */
        this.stepSimulation(delta, (int) maxSubSteps, delta/maxSubSteps);
    }

    /**
     * Add an {@link Entity} which has a {@link PhysicsComposition}.
     * Throws a {@link PhysicsWorldTrackingException} if the {@link Entity}
     * doesn't have a {@link PhysicsComposition} stitched to it.
     * @param entity The {@link Entity} to add
     */
    public void track(Entity entity) throws PhysicsWorldTrackingException {
        if (!Rayon.hasPhysics(entity)) {
            throw new PhysicsWorldTrackingException("Cannot add entity without PhysicsComposition");
        }

        this.entities.add(entity);
    }

    /**
     * Stop tracking an {@link Entity} within the {@link PhysicsWorld}.
     * @param entity The {@link Entity} to stop tracking
     */
    public void stopTracking(Entity entity) {
        this.entities.remove(entity);
    }

    /**
     * Get a list of rigid bodies. Includes blocks.
     * @return a list of rigid bodies
     */
    public List<RigidBody> getRigidBodies() {
        List<RigidBody> bodies = Lists.newArrayList();

        /* Add all blocks. */
        bodies.addAll(this.blockCollisionHelper.getRigidBodies());

        /* Add all entities. */
        entities.forEach(entity -> {
            PhysicsComposition physics = Rayon.getPhysics(entity);

            if (physics != null) {
                bodies.add(physics.getRigidBody());
            }
        });

        return bodies;
    }

    /**
     * Get the {@link BlockCollisionHelper} object.
     * @return the helper used for loading/unloading blocks
     */
    public BlockCollisionHelper getBlockCollisionHelper() {
        return this.blockCollisionHelper;
    }

    /**
     * Get the list of physics objects.
     * @return the list of physics objects
     */
    public List<Entity> getEntities() {
        return this.entities;
    }

    /**
     * @return the debug renderer attribute
     */
    public DebugRenderer getDebugRenderer() {
        return this.debugRenderer;
    }

    /**
     * Draw the object's rigid body shape. Original code has no implementation of this :(
     * @param worldTransform the position and rotation
     * @param shape the rigid body itself
     * @param color the color of the outline
     */
    @Override
    public void debugDrawObject(Transform worldTransform, CollisionShape shape, Vector3f color) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

        /* The distance to the camera */
        Vector3f pos = new Vector3f(worldTransform.origin);
        pos.sub(VectorHelper.vec3dToVector3f(camera.getPos()));

        /* Compound Shape */
		if (shape.getShapeType() == BroadphaseNativeType.COMPOUND_SHAPE_PROXYTYPE) {
            for (CompoundShapeChild child : ((CompoundShape) shape).getChildList()) {
                debugDrawObject(child.transform, child.childShape, color);
            }
		} else {

		    /* Concave Mesh */
            if (shape.isConcave()) {
                ConcaveShape concaveMesh = (ConcaveShape) shape;

                Vector3f aabbMax = new Vector3f((float) 1e30, (float) 1e30, (float) 1e30);
                Vector3f aabbMin = new Vector3f((float) -1e30, (float) -1e30, (float) -1e30);

                concaveMesh.processAllTriangles(null, aabbMin, aabbMax);

            /* Convex Mesh */
            } else if (shape.getShapeType() == BroadphaseNativeType.CONVEX_TRIANGLEMESH_SHAPE_PROXYTYPE) {
                BvhTriangleMeshShape convexMesh = (BvhTriangleMeshShape) shape;

                Vector3f aabbMax = new Vector3f((float) 1e30, (float) 1e30, (float) 1e30);
                Vector3f aabbMin = new Vector3f((float) -1e30, (float) -1e30, (float) -1e30);

                convexMesh.getMeshInterface().internalProcessAllTriangles(null, aabbMin, aabbMax);

            /* Polyhedral Shape (most common) */
            } else if (shape.isPolyhedral()) {
                PolyhedralConvexShape polyshape = (PolyhedralConvexShape) shape;

                for (int i = 0; i < polyshape.getNumEdges(); i++) {
                    Vector3f a = new Vector3f();
                    Vector3f b = new Vector3f();

                    polyshape.getEdge(i, a, b);
                    getDebugDrawer().drawLine(a, b, color);
                }
            }
        }
    }
}
