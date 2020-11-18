package dev.lazurite.api.physics.client;

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
import dev.lazurite.api.physics.client.handler.ClientPhysicsHandler;
import dev.lazurite.api.physics.client.helper.BlockCollisionHelper;
import dev.lazurite.api.physics.client.render.DebugRenderer;
import dev.lazurite.api.physics.util.math.VectorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PhysicsWorld extends DiscreteDynamicsWorld {
    public static final int BLOCK_RADIUS = 3;
    public static final float AIR_DENSITY = 1.2f;
    public static final float GRAVITY = -9.81f;

    private final List<ClientPhysicsHandler> entities;
    private final BlockCollisionHelper blockCollisions;
    private final DebugRenderer debugRenderer;
    private final Clock clock;

    private static PhysicsWorld instance;

    public PhysicsWorld(CollisionDispatcher dispatcher, BroadphaseInterface broadphase, SequentialImpulseConstraintSolver solver, CollisionConfiguration collisionConfiguration) {
        super(dispatcher, broadphase, solver, collisionConfiguration);

        this.clock = new Clock();
        this.entities = new ArrayList<>();
        this.blockCollisions = new BlockCollisionHelper(this);
        this.debugRenderer = new DebugRenderer(this);
        this.setDebugDrawer(debugRenderer);
    }

    public static void create() {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        PhysicsWorld.instance = new PhysicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        PhysicsWorld.instance.setGravity(new Vector3f(0, GRAVITY, 0));
    }

    public void stepWorld() {
        ClientWorld world = ClientInitializer.client.world;
        List<ClientPhysicsHandler> toRemove = new ArrayList<>();

        float delta = clock.getTimeMicroseconds() / 1000000F;
        float maxSubSteps = 5.0f;
        clock.reset();

        this.entities.forEach(physics -> {
            if (physics.getEntity().removed) {
                toRemove.add(physics);
            }

            if (world != null) {
                if (physics.isActive()) {
                    physics.getEntity().step(delta);

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

    public void add(ClientPhysicsHandler physics) {
        this.entities.add(physics);
    }

    public void remove(ClientPhysicsHandler physics) {
        this.removeRigidBody(physics.getRigidBody());
        this.entities.remove(physics);
    }

    public List<RigidBody> getRigidBodies() {
        List<RigidBody> bodies = new ArrayList<>();

        entities.forEach(physics -> bodies.add(physics.getRigidBody()));
        bodies.addAll(this.blockCollisions.getRigidBodies());

        return bodies;
    }

    public BlockCollisionHelper getBlockCollisions() {
        return this.blockCollisions;
    }

    public List<ClientPhysicsHandler> getEntities() {
        return this.entities;
    }

    public DebugRenderer getDebugRenderer() {
        return this.debugRenderer;
    }

    public Clock getClock() {
        return this.clock;
    }

    public static PhysicsWorld getInstance() {
        return instance;
    }

    @Override
    public void debugDrawObject(Transform worldTransform, CollisionShape shape, Vector3f color) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

        Vector3f pos = new Vector3f(worldTransform.origin);
        pos.sub(VectorHelper.vec3dToVector3f(camera.getPos()));

        for (ClientPhysicsHandler handler : entities) {
            RigidBody body = handler.getRigidBody();

            if (body.getWorldTransform(new Transform()) == worldTransform && body.getCollisionShape() == shape) {
                return;
            }
        }

		if (shape.getShapeType() == BroadphaseNativeType.COMPOUND_SHAPE_PROXYTYPE) {
			CompoundShape compoundShape = (CompoundShape) shape;
			for (int i = compoundShape.getNumChildShapes() - 1; i>=0; i--) {
				Transform childTrans = compoundShape.getChildTransform(i, new Transform());
				CollisionShape colShape = compoundShape.getChildShape(i);
				debugDrawObject(childTrans, colShape, color);
			}
		} else {
            if (shape.isConcave()) {
                ConcaveShape concaveMesh = (ConcaveShape) shape;

                Vector3f aabbMax = new Vector3f((float) 1e30, (float) 1e30, (float) 1e30);
                Vector3f aabbMin = new Vector3f((float) -1e30, (float) -1e30, (float) -1e30);

                concaveMesh.processAllTriangles(null, aabbMin, aabbMax);
            } else if (shape.getShapeType() == BroadphaseNativeType.CONVEX_TRIANGLEMESH_SHAPE_PROXYTYPE) {
                BvhTriangleMeshShape convexMesh = (BvhTriangleMeshShape) shape;

                Vector3f aabbMax = new Vector3f((float) 1e30, (float) 1e30, (float) 1e30);
                Vector3f aabbMin = new Vector3f((float) -1e30, (float) -1e30, (float) -1e30);

                convexMesh.getMeshInterface().internalProcessAllTriangles(null, aabbMin, aabbMax);
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
