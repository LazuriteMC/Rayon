package dev.lazurite.rayon.impl.physics.body.type;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;

/**
 * Any {@link PhysicsRigidBody} with this assigned will
 * be stepped in {@link MinecraftDynamicsWorld} during
 * each simulation step.
 *
 * @see MinecraftDynamicsWorld#step
 */
public interface SteppableBody {
    void step(float delta);
}
