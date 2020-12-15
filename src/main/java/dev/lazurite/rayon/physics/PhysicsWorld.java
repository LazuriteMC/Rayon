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
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.composition.DynPhysicsComposition;
import dev.lazurite.rayon.physics.helper.BlockCollisionHelper;
import dev.lazurite.rayon.render.DebugRenderer;
import dev.lazurite.rayon.physics.helper.VectorHelper;
import dev.lazurite.rayon.util.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main world class behind most of the Physics API. Every physics object, whether block
 * or entity, ends up in here and is tracked within this class. There are also values which can be changed
 * (only in the code) such as gravity and air density.
 * @author Ethan Johnson
 */
@Environment(EnvType.CLIENT)
public final class PhysicsWorld extends DiscreteDynamicsWorld {
    private final List<Entity> entities;
    private final BlockCollisionHelper blockCollisions;
    private final DebugRenderer debugRenderer;
    private final Clock clock;

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
        this.blockCollisions = new BlockCollisionHelper(this);
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
        World world = MinecraftClient.getInstance().world;
        List<Entity> toRemove = new ArrayList<>();
        float maxSubSteps = 5.0f;

        float delta = clock.getTimeMicroseconds() / 1000000F;
        clock.reset();

        this.entities.forEach(entity -> {
            if (entity.removed) {
                toRemove.add(entity);
                return;
            }

            DynPhysicsComposition physics = Rayon.getPhysics(entity);

            if (world != null) {
                if (physics.isActive()) {
                    physics.step(entity, delta);

                    /* Add the rigid body to the world if it isn't already there */
                    if (!physics.getRigidBody().isInWorld()) {
                        this.addRigidBody(physics.getRigidBody());
                    }

                    /* Load in block collisions */
                    if (!physics.getEntity().noClip) {
                        this.blockCollisions.load(physics.getEntity(), world);
                    }
                } else {
                    /* Remove the rigid body if it is in the world */
                    if (physics.getRigidBody().isInWorld()) {
                        this.removeRigidBody(physics.getRigidBody());
                    }
                }
            }
        });

        this.blockCollisions.unload();
        toRemove.forEach(entities::remove);
        this.stepSimulation(delta, (int) maxSubSteps, delta/maxSubSteps);
    }

    /**
     * Add a physics object into the world.
     * @param physics The {@link PhysicsHandler} from a {@link PhysicsEntity}
     */
    public void add(ClientPhysicsHandler physics) {
        this.entities.add(physics);
    }

    /**
     * Remove a physics object from the world.
     * @param physics The {@link PhysicsHandler} from a {@link PhysicsEntity}
     */
    public void remove(ClientPhysicsHandler physics) {
        this.removeRigidBody(physics.getRigidBody());
        this.entities.remove(physics);
    }

    /**
     * Get a list of rigid bodies. Includes blocks
     * @return a list of rigid bodies
     */
    public List<RigidBody> getRigidBodies() {
        List<RigidBody> bodies = new ArrayList<>();

        entities.forEach(physics -> bodies.add(physics.getRigidBody()));
        bodies.addAll(this.blockCollisions.getRigidBodies());

        return bodies;
    }

    /**
     * Get the list of blocks from {@link BlockCollisionHelper}.
     * @return the list of blocks
     */
    public BlockCollisionHelper getBlockCollisions() {
        return this.blockCollisions;
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
