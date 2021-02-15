package dev.lazurite.rayon.impl.bullet.body.type;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.lazurite.rayon.impl.bullet.manager.FluidManager;

/**
 * Any {@link PhysicsRigidBody} with this interface assigned will be subject
 * to air resistance using it's drag coefficient value.
 * @see FluidManager
 */
public interface CustomDragBody {
    float getDragCoefficient();
}
