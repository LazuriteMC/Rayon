package dev.lazurite.rayon.client.handler;

import dev.lazurite.rayon.server.entity.PhysicsEntity;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * A PhysicsHandler implementation meant to run on the server-side of Minecraft.
 * @author Ethan Johnson
 */
public class ServerPhysicsHandler implements PhysicsHandler {
    private final PhysicsEntity entity;
    private final Vector3f position;
    private final Vector3f linearVelocity;
    private final Vector3f angularVelocity;
    private final Quat4f orientation;

    /**
     * The constructor which creates the {@link ServerPhysicsHandler}.
     * @param entity The {@link PhysicsEntity} being used
     */
    public ServerPhysicsHandler(PhysicsEntity entity) {
        this.entity = entity;

        position = new Vector3f();
        linearVelocity = new Vector3f();
        angularVelocity = new Vector3f();
        orientation = new Quat4f();
    }

    /**
     * @param position the position vector
     */
    @Override
    public void setPosition(Vector3f position) {
        this.position.set(position);
        this.entity.markDirty();
    }

    /**
     * @return the position vector
     */
    @Override
    public Vector3f getPosition() {
        return this.position;
    }

    /**
     * @param linearVelocity the linear velocity vector
     */
    @Override
    public void setLinearVelocity(Vector3f linearVelocity) {
        this.linearVelocity.set(linearVelocity);
        this.entity.markDirty();
    }

    /**
     * @return the linear velocity vector
     */
    @Override
    public Vector3f getLinearVelocity() {
        return this.linearVelocity;
    }

    /**
     * @param angularVelocity the angular velocity vector
     */
    @Override
    public void setAngularVelocity(Vector3f angularVelocity) {
        this.angularVelocity.set(angularVelocity);
        this.entity.markDirty();
    }

    /**
     * @return the angular velocity vector
     */
    @Override
    public Vector3f getAngularVelocity() {
        return this.angularVelocity;
    }

    /**
     * @param orientation the rotation quaternion
     */
    @Override
    public void setOrientation(Quat4f orientation) {
        this.orientation.set(orientation);
        this.entity.markDirty();
    }

    /**
     * @return the rotation quaternion
     */
    @Override
    public Quat4f getOrientation() {
        return this.orientation;
    }

    /**
     * @return the {@link PhysicsEntity} being tracked
     */
    @Override
    public PhysicsEntity getEntity() {
        return entity;
    }
}
