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
import dev.lazurite.api.physics.util.math.QuaternionHelper;
import dev.lazurite.api.physics.server.entity.PhysicsEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

@Environment(EnvType.CLIENT)
public class ClientPhysicsHandler implements PhysicsHandler {
    private final Quat4f prevOrientation;
    private final Quat4f netOrientation;
    private final PhysicsEntity entity;
    private RigidBody body;

    private int prevSize = -1;
    private float prevMass = -1;

    public ClientPhysicsHandler(PhysicsEntity entity) {
        this.entity = entity;
        this.prevOrientation = new Quat4f(0, 1, 0, 0);
        this.netOrientation = new Quat4f(0, 1, 0, 0);

        this.createRigidBody();
        ClientInitializer.physicsWorld.add(this);
    }

    /**
     * Gets whether the entity is active. It is active when the {@link RigidBody}
     * is in the {@link PhysicsWorld}.
     * @return whether or not the entity is active
     */
    public boolean isActive() {
        return entity.age > 1 && entity.getValue(PhysicsEntity.PLAYER_ID) == ClientInitializer.client.player.getEntityId();
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
     * @return the drone's current {@link RigidBody}
     */
    public RigidBody getRigidBody() {
        return this.body;
    }

    @Override
    public PhysicsEntity getEntity() {
        return entity;
    }

    /**
     * Sets the position of the {@link RigidBody}.
     * @param vec the new position
     */
    @Override
    public void setPosition(Vector3f vec) {
        Transform trans = this.body.getWorldTransform(new Transform());
        trans.origin.set(vec);
        this.body.setWorldTransform(trans);
    }

    @Override
    public Vector3f getPosition() {
        return this.body.getCenterOfMassPosition(new Vector3f());
    }

    @Override
    public void setLinearVelocity(Vector3f linearVelocity) {
        this.body.setLinearVelocity(linearVelocity);
    }

    @Override
    public Vector3f getLinearVelocity() {
        return this.body.getLinearVelocity(new Vector3f());
    }

    @Override
    public void setAngularVelocity(Vector3f angularVelocity) {
        this.body.setAngularVelocity(angularVelocity);
    }

    @Override
    public Vector3f getAngularVelocity() {
        return this.body.getAngularVelocity(new Vector3f());
    }

    /**
     * Sets the orientation of the {@link RigidBody}.
     * @param q the new orientation
     */
    @Override
    public void setOrientation(Quat4f q) {
        Transform trans = this.body.getWorldTransform(new Transform());
        trans.setRotation(q);
        this.body.setWorldTransform(trans);
    }

    /**
     * Gets the orientation of the {@link RigidBody}.
     * @return a new {@link Quat4f} containing orientation
     */
    @Override
    public Quat4f getOrientation() {
        return this.body.getWorldTransform(new Transform()).getRotation(new Quat4f());
    }

    /**
     * Sets the previous orientation of the {@link PhysicsHandler}.
     * @param prevOrientation the new previous orientation
     */
    public void setPrevOrientation(Quat4f prevOrientation) {
        this.prevOrientation.set(prevOrientation);
    }

    /**
     * Gets the previous orientation of the {@link PhysicsHandler}.
     * @return a new previous orientation
     */
    public Quat4f getPrevOrientation() {
        Quat4f out = new Quat4f();
        out.set(prevOrientation);
        return out;
    }

    /**
     * Sets the orientation received over the network.
     * @param netOrientation the new net orientation
     */
    public void setNetOrientation(Quat4f netOrientation) {
        this.netOrientation.set(netOrientation);
    }

    /**
     * Gets the orientation received over the network.
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
     * Creates a new {@link RigidBody} based off of the drone's attributes.
     */
    public void createRigidBody() {
        System.out.println("CREATE: " + entity.getValue(PhysicsEntity.MASS));
        float s = entity.getValue(PhysicsEntity.SIZE) / 16.0f;
        Box cBox = new Box(-s / 2.0f, -s / 8.0f, -s / 2.0f, s / 2.0f, s / 8.0f, s / 2.0f);
        Vector3f inertia = new Vector3f(0.0F, 0.0F, 0.0F);
        Vector3f box = new Vector3f(
                ((float) (cBox.maxX - cBox.minX) / 2.0F) + 0.005f,
                ((float) (cBox.maxY - cBox.minY) / 2.0F) + 0.005f,
                ((float) (cBox.maxZ - cBox.minZ) / 2.0F) + 0.005f);
        CollisionShape shape = new BoxShape(box);
        shape.calculateLocalInertia(entity.getValue(PhysicsEntity.MASS), inertia);

        Vec3d pos = entity.getPos();
        Vector3f position = new Vector3f((float) pos.x, (float) pos.y + 0.125f, (float) pos.z);

        DefaultMotionState motionState;
        if (getRigidBody() != null) {
            RigidBody old = getRigidBody();
            motionState = new DefaultMotionState(old.getWorldTransform(new Transform()));
            ClientInitializer.physicsWorld.removeRigidBody(old);
        } else {
            motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 1, 0, 0), position, 1.0f)));
        }

        RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(entity.getValue(PhysicsEntity.MASS), motionState, shape, inertia);
        RigidBody body = new RigidBody(ci);
        body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        ClientInitializer.physicsWorld.addRigidBody(body);
        this.body = body;
    }
}
