package dev.lazurite.api.client.physics.handler;

import dev.lazurite.api.server.entity.PhysicsEntity;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public interface PhysicsHandler {
    Vector3f getPosition();
    Vector3f getLinearVelocity();
    Vector3f getAngularVelocity();
    Quat4f getOrientation();
    PhysicsEntity getEntity();

    void setPosition(Vector3f position);
    void setLinearVelocity(Vector3f linearVelocity);
    void setAngularVelocity(Vector3f angularVelocity);
    void setOrientation(Quat4f orientation);
}
