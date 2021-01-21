package dev.lazurite.rayon.impl.util.exception;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;

/**
 * A custom runtime exception relating to {@link PhysicsRigidBody} objects.
 * @see EntityRigidBody
 */
public class RigidBodyException extends RuntimeException {
    public RigidBodyException(String message) {
        super(message);
    }
}
