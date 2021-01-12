package dev.lazurite.rayon.util.exception;

import dev.lazurite.rayon.physics.body.EntityRigidBody;
import com.bulletphysics.dynamics.RigidBody;

/**
 * A custom runtime exception relating to {@link RigidBody} objects.
 * @see EntityRigidBody
 */
public class RigidBodyException extends RuntimeException {
    public RigidBodyException(String message) {
        super(message);
    }
}
