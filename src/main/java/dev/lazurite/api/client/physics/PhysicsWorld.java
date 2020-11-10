package dev.lazurite.api.client.physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;
import dev.lazurite.api.client.physics.handler.ClientPhysicsHandler;
import dev.lazurite.api.LazuriteAPI;
import dev.lazurite.api.client.LazuriteClient;
import dev.lazurite.api.network.tracker.Config;
import dev.lazurite.api.client.physics.helper.BlockCollisionHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PhysicsWorld {
    public static final Config.Key<Integer> BLOCK_RADIUS = new Config.Key<>("blockRadius", LazuriteAPI.INTEGER_TYPE);
    public static final Config.Key<Float> AIR_DENSITY = new Config.Key<>("airDensity", LazuriteAPI.FLOAT_TYPE);
    public static final Config.Key<Float> GRAVITY = new Config.Key<>("gravity", LazuriteAPI.FLOAT_TYPE);

    public final Clock clock;
    public final Config config;
    public final List<ClientPhysicsHandler> entities;
    public final BlockCollisionHelper blockCollisions;
    private final DiscreteDynamicsWorld dynamicsWorld;

    public PhysicsWorld() {
        this.entities = new ArrayList<>();
        this.blockCollisions = new BlockCollisionHelper(this);
        this.clock = new Clock();
        this.config = LazuriteClient.config;

        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0, config.getValue(GRAVITY), 0));
    }

    public void stepWorld() {
        ClientWorld world = LazuriteClient.client.world;
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

    public void setGravity() {
        dynamicsWorld.setGravity(new Vector3f(0, config.getValue(GRAVITY), 0));
    }

    public float getAirDensity() {
        return config.getValue(AIR_DENSITY);
    }

    public int getBlockRadius() {
        return config.getValue(BLOCK_RADIUS);
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
