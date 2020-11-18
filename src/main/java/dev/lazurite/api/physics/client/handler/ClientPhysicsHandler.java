package dev.lazurite.api.physics.client.handler;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.api.physics.client.ClientInitializer;
import dev.lazurite.api.physics.client.PhysicsWorld;
import dev.lazurite.api.physics.client.helper.ShapeHelper;
import dev.lazurite.api.physics.util.math.QuaternionHelper;
import dev.lazurite.api.physics.server.entity.PhysicsEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
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
    private final PhysicsEntity entity;
    private RigidBody body;

    private int prevSize = -1;
    private float prevMass = -1;

    /**
     * The constructor which sets up the {@link PhysicsHandler} and creates a new {@link RigidBody}.
     * @param entity
     */
    public ClientPhysicsHandler(PhysicsEntity entity) {
        this.entity = entity;
        this.prevOrientation = new Quat4f(0, 1, 0, 0);
        this.netOrientation = new Quat4f(0, 1, 0, 0);
        PhysicsWorld.getInstance().add(this);
        this.createRigidBody();
    }

    /**
     * Gets whether the entity is active. It is active when the {@link RigidBody}
     * is in the {@link PhysicsWorld}.
     * @return whether or not the entity is active
     */
    public boolean isActive() {
        PlayerEntity player = ClientInitializer.client.player;
        if (player != null)
            return entity.age > 1 && entity.getValue(PhysicsEntity.PLAYER_ID) == player.getEntityId();
        return false;
    }

    /**
     * Rotate the entity's {@link Quat4f} by the given degrees on the X axis.
     * @param deg degrees to rotate by
     */
    public void rotateX(float deg) {
        Quat4f quat = getOrientation();
        QuaternionHelper.rotateX(quat, deg);
        setOrientation(quat);
    }

    /**
     * Rotate the entity's {@link Quat4f} by the given degrees on the Y axis.
     * @param deg degrees to rotate by
     */
    public void rotateY(float deg) {
        Quat4f quat = getOrientation();
        QuaternionHelper.rotateY(quat, deg);
        setOrientation(quat);
    }

    /**
     * Rotate the entity's {@link Quat4f} by the given degrees on the Z axis.
     * @param deg degrees to rotate by
     */
    public void rotateZ(float deg) {
        Quat4f quat = getOrientation();
        QuaternionHelper.rotateZ(quat, deg);
        setOrientation(quat);
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
     * @param prevOrientation the previous rotation quaternion
     */
    public void setPrevOrientation(Quat4f prevOrientation) {
        this.prevOrientation.set(prevOrientation);
    }

    /**
     * @return the previous rotation quaternion
     */
    public Quat4f getPrevOrientation() {
        Quat4f out = new Quat4f();
        out.set(prevOrientation);
        return out;
    }

    /**
     * @param netOrientation the net rotation quaternion
     */
    public void setNetOrientation(Quat4f netOrientation) {
        this.netOrientation.set(netOrientation);
    }

    /**
     * @return the net rotation quaternion
     */
    public Quat4f getNetOrientation() {
        Quat4f out = new Quat4f();
        out.set(netOrientation);
        return out;
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
     * Sets the size of the physics entity. Also refreshes the {@link RigidBody}.
     * @param size the new size
     */
    public void setSize(int size) {
        if (prevSize != size) {
           createRigidBody();
        }

        prevSize = size;
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
        float s = entity.getValue(PhysicsEntity.SIZE) / 16.0f;
        Box cBox = new Box(-s / 2.0f, -s / 8.0f, -s / 2.0f, s / 2.0f, s / 8.0f, s / 2.0f);
        Vector3f inertia = new Vector3f(0.0F, 0.0F, 0.0F);
        Vector3f box = new Vector3f(
                ((float) (cBox.maxX - cBox.minX) / 2.0F) + 0.005f,
                ((float) (cBox.maxY - cBox.minY) / 2.0F) + 0.005f,
                ((float) (cBox.maxZ - cBox.minZ) / 2.0F) + 0.005f);

        CollisionShape shape;
        if (ShapeHelper.shape != null) {
            shape = ShapeHelper.shape;
        } else {
            shape = new BoxShape(box);
        }
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

        RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(entity.getValue(PhysicsEntity.MASS), motionState, shape, inertia);
        RigidBody body = new RigidBody(ci);
        body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        this.body = body;
    }
}
