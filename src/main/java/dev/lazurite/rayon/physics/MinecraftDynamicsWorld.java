package dev.lazurite.rayon.physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import com.google.common.collect.Lists;
import dev.lazurite.rayon.physics.helper.BlockHelper;
import dev.lazurite.rayon.physics.helper.DebugHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.physics.util.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.List;

public class MinecraftDynamicsWorld extends DiscreteDynamicsWorld {
    private final List<RigidBody> rigidBodies;
    private final List<Entity> entities;
    private final BlockHelper blockHelper;
    private final World world;

    public MinecraftDynamicsWorld(World world, Dispatcher dispatcher, BroadphaseInterface pairCache, ConstraintSolver constraintSolver, CollisionConfiguration collisionConfiguration) {
        super(dispatcher, pairCache, constraintSolver, collisionConfiguration);

        this.world = world;
        this.rigidBodies = Lists.newArrayList();
        this.entities = Lists.newArrayList();
        this.blockHelper = new BlockHelper(this);

        this.setDebugDrawer(new DebugHelper(this));
        this.setGravity(new Vector3f(0, Constants.GRAVITY, 0));
    }

    public List<RigidBody> getRigidBodies() {
        return Lists.newArrayList(rigidBodies);
    }

    public List<Entity> getEntities() {
        return Lists.newArrayList(entities);
    }

    @Override
    public int stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep) {
        this.blockHelper.load(getEntities(), world);

        this.blockHelper.unload();
        return super.stepSimulation(timeStep, maxSubSteps, fixedTimeStep);
    }

    /**
     * Draw the object's rigid body shape. Original code has no implementation of this :(
     * @param worldTransform the position and rotation
     * @param shape the rigid body itself
     * @param color the color of the outline
     */
    @Override
    @Environment(EnvType.CLIENT)
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
