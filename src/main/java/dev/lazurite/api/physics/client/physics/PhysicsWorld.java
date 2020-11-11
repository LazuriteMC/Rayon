package dev.lazurite.api.physics.client.physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;
import dev.lazurite.api.physics.client.ClientInitializer;
import dev.lazurite.api.physics.client.physics.handler.ClientPhysicsHandler;
import dev.lazurite.api.physics.client.physics.helper.BlockCollisionHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PhysicsWorld {
    public static final int BLOCK_RADIUS = 3;
    public static final float AIR_DENSITY = 1.2f;
    public static final float GRAVITY = 9.81f;

    public final List<ClientPhysicsHandler> entities;
    public final BlockCollisionHelper blockCollisions;
    private final DiscreteDynamicsWorld dynamicsWorld;
    public final Clock clock;

    public PhysicsWorld() {
        this.entities = new ArrayList<>();
        this.blockCollisions = new BlockCollisionHelper(this);
        this.clock = new Clock();

        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0, GRAVITY, 0));
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
                        this.dynamicsWorld.addRigidBody(physics.getRigidBody());
                    }

                    /* Load in block collisions */
                    if (!physics.getEntity().noClip) {
                        this.blockCollisions.load(physics.getEntity(), world);
                    }
                } else {
                    /* Remove the rigid body if it is in the world */
                    if (physics.getRigidBody().isInWorld()) {
                        this.dynamicsWorld.removeRigidBody(physics.getRigidBody());
                    }
                }
            }
        });

        this.blockCollisions.unload();
        toRemove.forEach(entities::remove);
        this.dynamicsWorld.stepSimulation(delta, (int) maxSubSteps, delta/maxSubSteps);
    }

    public void add(ClientPhysicsHandler physics) {
        this.entities.add(physics);
    }

    public void remove(ClientPhysicsHandler physics) {
        this.dynamicsWorld.removeRigidBody(physics.getRigidBody());
        this.entities.remove(physics);
    }

    public void addRigidBody(RigidBody body) {
        this.dynamicsWorld.addRigidBody(body);
    }

    public void removeRigidBody(RigidBody body) {
        this.dynamicsWorld.removeRigidBody(body);
    }

    public List<RigidBody> getRigidBodies() {
        List<RigidBody> bodies = new ArrayList<>();

        entities.forEach(physics -> bodies.add(physics.getRigidBody()));
        bodies.addAll(this.blockCollisions.getRigidBodies());

        return bodies;
    }

    public DiscreteDynamicsWorld getDynamicsWorld() {
        return this.dynamicsWorld;
    }

    public BlockCollisionHelper getBlockCollisions() {
        return this.blockCollisions;
    }
}
