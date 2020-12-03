package dev.lazurite.rayon.physics.handler;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.side.client.ClientInitializer;
import dev.lazurite.rayon.side.client.PhysicsWorld;
import dev.lazurite.rayon.helper.QuaternionHelper;
import dev.lazurite.rayon.side.server.entity.PhysicsEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * A PhysicsHandler implementation meant to run on the client-side of Minecraft.
 * @author Ethan Johnson
 */
@Environment(EnvType.CLIENT)
public class ClientPhysicsHandler implements PhysicsHandler {
    private final Quat4f prevOrientation;
    private final Quat4f netOrientation;

    /** The entity that this handler belongs to. */
    private final PhysicsEntity entity;

    /** The shape of the rigid body. */
    private final CollisionShape shape;

    private float prevMass = -1;
    private RigidBody body;

    /**
     * The constructor which sets up the {@link PhysicsHandler} and creates a new {@link RigidBody}.
     * @param entity the physics entity
     */
    public ClientPhysicsHandler(PhysicsEntity entity, CollisionShape shape) {
        this.entity = entity;
        this.shape = shape;

        this.prevOrientation = new Quat4f(0, 1, 0, 0);
        this.netOrientation = new Quat4f(0, 1, 0, 0);

        this.createRigidBody();
        PhysicsWorld.getInstance().add(this);
    }

    /**
     * Gets whether the entity is active. It is active when the {@link RigidBody}
     * is in the {@link PhysicsWorld}.
     * @return whether or not the entity is active
     */
    @Override
    public boolean isActive() {
        PlayerEntity player = ClientInitializer.client.player;

        if (player != null) {
            return entity.age > 5 && entity.getValue(PhysicsEntity.PLAYER_ID) == player.getEntityId();
        }

        return false;
    }



    /**
     * Gets the {@link RigidBody}.
     * @return the current {@link RigidBody}
     */
    public RigidBody getRigidBody() {
        return this.body;
    }

    /**
     * Gets the {@link PhysicsEntity}.
     * @return the current {@link PhysicsEntity}
     */
    @Override
    public PhysicsEntity getEntity() {
        return entity;
    }

    /**
     * @param vec the position vector
     */
    @Override
    public void setPosition(Vector3f vec) {
        Transform trans = this.body.getWorldTransform(new Transform());
        trans.origin.set(vec);
        this.body.setWorldTransform(trans);
    }

    /**
     * @return the position vector
     */
    @Override
    public Vector3f getPosition() {
        return this.body.getCenterOfMassPosition(new Vector3f());
    }

    /**
     * @param linearVelocity the linear velocity vector
     */
    @Override
    public void setLinearVelocity(Vector3f linearVelocity) {
        this.body.setLinearVelocity(linearVelocity);
    }

    /**
     * @return the linear velocity vector
     */
    @Override
    public Vector3f getLinearVelocity() {
        return this.body.getLinearVelocity(new Vector3f());
    }

    /**
     * @param angularVelocity the angular velocity vector
     */
    @Override
    public void setAngularVelocity(Vector3f angularVelocity) {
        this.body.setAngularVelocity(angularVelocity);
    }

    /**
     * @return the angular velocity vector
     */
    @Override
    public Vector3f getAngularVelocity() {
        return this.body.getAngularVelocity(new Vector3f());
    }

    /**
     * @param q the rotation quaternion
     */
    @Override
    public void setOrientation(Quat4f q) {
        Transform trans = this.body.getWorldTransform(new Transform());
        trans.setRotation(q);
        this.body.setWorldTransform(trans);
    }

    /**
     * @return the rotation quaternion
     */
    @Override
    public Quat4f getOrientation() {
        return this.body.getWorldTransform(new Transform()).getRotation(new Quat4f());
    }

    /**
     * @param delta delta time
     * @return the slerped rotation quaternion
     */
    public Quat4f getOrientation(float delta) {
        if (isActive()) {
            return getOrientation();
        }

        return QuaternionHelper.slerp(prevOrientation, getOrientation(), delta);
    }

    /**
     * Changes prevOrientation and sets the current
     * orientation to netOrientation.
     */
    public void updateNetOrientation() {
        prevOrientation.set(getOrientation());
        setOrientation(netOrientation);
    }

    /**
     * @param netOrientation the net rotation quaternion
     */
    public void setNetOrientation(Quat4f netOrientation) {
        this.netOrientation.set(netOrientation);
    }

    /**
     * Sets the mass of the physics entity. Also refreshes the {@link RigidBody}.
     * @param mass the new mass
     */
    public void setMass(float mass) {
        if (prevMass != mass) {
            createRigidBody();
        }

        prevMass = mass;
    }

    /**
     * Apply a list of forces. Mostly a convenience method.
     * @param forces an array of forces to apply to the {@link RigidBody}
     */
    public void applyForce(Vector3f... forces) {
        for (Vector3f force : forces) {
            getRigidBody().applyCentralForce(force);
        }
    }

    /**
     * Creates a new {@link RigidBody} based off of the entity's attributes.
     */
    public void createRigidBody() {
        Vector3f inertia = new Vector3f(0.0F, 0.0F, 0.0F);
        shape.calculateLocalInertia(entity.getValue(PhysicsEntity.MASS), inertia);

        Vec3d pos = entity.getPos();
        Vector3f position = new Vector3f((float) pos.x, (float) pos.y + 0.125f, (float) pos.z);

        DefaultMotionState motionState;
        if (getRigidBody() != null) {
            RigidBody old = getRigidBody();
            motionState = new DefaultMotionState(old.getWorldTransform(new Transform()));
            PhysicsWorld.getInstance().removeRigidBody(old);
        } else {
            motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 1, 0, 0), position, 1.0f)));
        }

        System.out.println(entity.getValue(PhysicsEntity.MASS));
        RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(entity.getValue(PhysicsEntity.MASS), motionState, shape, inertia);
        RigidBody body = new RigidBody(ci);
        body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        this.body = body;
    }
}
