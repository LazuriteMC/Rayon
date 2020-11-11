package dev.lazurite.api.physics.client.physics.handler;

import dev.lazurite.api.physics.server.entity.PhysicsEntity;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class ServerPhysicsHandler implements PhysicsHandler {
    private final PhysicsEntity entity;
    private final Vector3f position;
    private final Vector3f linearVelocity;
    private final Vector3f angularVelocity;
    private final Quat4f orientation;

    public ServerPhysicsHandler(PhysicsEntity entity) {
        this.entity = entity;

        position = new Vector3f();
        linearVelocity = new Vector3f();
        angularVelocity = new Vector3f();
        orientation = new Quat4f();
    }

    @Override
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    @Override
    public Vector3f getPosition() {
        return this.position;
    }

    @Override
    public void setLinearVelocity(Vector3f linearVelocity) {
        this.linearVelocity.set(linearVelocity);
    }

    @Override
    public Vector3f getLinearVelocity() {
        return this.linearVelocity;
    }

    @Override
    public void setAngularVelocity(Vector3f angularVelocity) {
        this.angularVelocity.set(angularVelocity);
    }

    @Override
    public Vector3f getAngularVelocity() {
        return this.angularVelocity;
    }

    @Override
    public void setOrientation(Quat4f orientation) {
        this.orientation.set(orientation);
    }

    @Override
    public Quat4f getOrientation() {
        return this.orientation;
    }

    @Override
    public PhysicsEntity getEntity() {
        return entity;
    }
}
