package dev.lazurite.api.physics.client.handler;

import dev.lazurite.api.physics.server.entity.PhysicsEntity;

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
