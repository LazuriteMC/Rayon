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
import dev.lazurite.rayon.exception.DynamicBodyException;
import dev.lazurite.rayon.physics.helper.BlockHelper;
import dev.lazurite.rayon.physics.helper.DebugHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.util.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import javax.vecmath.Vector3f;
import java.io.InputStreamReader;
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

    /** The {@link Clock} used for keeping time and calculating delta time in the main loop. */
    private final Clock clock;

    /** The instance variable used in place of instantiating {@link PhysicsWorld} yourself. */
    public static PhysicsWorld INSTANCE;

    /** The {@link BlockHelper} responsible for loading/unloading blocks from the {@link PhysicsWorld}. */
    public final BlockHelper blockHelper;

    /** The renderer responsible for showing outlines of {@link RigidBody} objects in-game. */
    public final DebugHelper debugHelper;

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

        this.entities = Lists.newArrayList();
        this.blockHelper = new BlockHelper(this);
        this.debugHelper = new DebugHelper(this);
        this.clock = new Clock();

        setDebugDrawer(debugHelper);
        setGravity(new Vector3f(0, Constants.GRAVITY, 0));
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

        PhysicsWorld.INSTANCE = new PhysicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
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
            if (!((DynamicBody) entity).hasDynamicBody()) {
                toRemove.add(entity);
                return;
            }

            /* Get the Physics Composition object for the given entity. */
//            DynamicBodyComposition physics = ((DynamicBody) entity).getDynamicBody();

            /* Build a list of entities to remove later on. */
            if (entity.removed) {
                toRemove.add(entity);
                return;
            }

//            if (world != null && physics.getRigidBody() != null) {
//                physics.step(entity, delta);
//
//                /* Add the rigid body to the world if it isn't already there */
//                if (!physics.getRigidBody().isInWorld()) {
//                    this.addRigidBody(physics.getRigidBody());
//                }
//
//                /* Load in block collisions */
//                if (!physics.getSynchronizer().get(DynamicBodyComposition.NO_CLIP)) {
//                    this.blockHelper.load(entity, world);
//                }
//            }
        });

        /* Clean out unnecessary blocks. */
        this.blockHelper.unload();

        /* Clean out all "to remove" entities. */
        toRemove.forEach(entities::remove);

        /* Step the world using delta. */
        this.stepSimulation(delta, (int) maxSubSteps, delta/maxSubSteps);
    }

    /**
     * Add an {@link Entity} which has a {@link DynamicBodyComposition}.
     * Adds a new {@link DynamicBodyComposition} if the {@link Entity}
     * doesn't have one stitched to it.
     * @param entity The {@link Entity} to add
     */
    public void track(Entity entity) {
        if (!((DynamicBody) entity).hasDynamicBody()) {
            if (entity instanceof LivingEntity) {
                throw new DynamicBodyException("Using physics with living entities is unsupported.");
            } else {
//                Thimble.stitch(DynamicBodyComposition::new, entity);
            }
        }

        if (!this.entities.contains(entity)) {
            this.entities.add(entity);
        }
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
        bodies.addAll(this.blockHelper.getRigidBodies());

        /* Add all entities. */
        entities.forEach(entity -> {
//            DynamicBodyComposition physics = ((DynamicBody) entity).getDynamicBody();
//
//            if (physics != null) {
//                bodies.add(physics.getRigidBody());
//            }
        });

        return bodies;
    }

    /**
     * Get the {@link BlockHelper} object.
     * @return the helper used for loading/unloading blocks
     */
    public BlockHelper getBlockHelper() {
        return this.blockHelper;
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
    public DebugHelper getDebugHelper() {
        return this.debugHelper;
    }

    public void addPropertyFile(InputStreamReader input) {

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
