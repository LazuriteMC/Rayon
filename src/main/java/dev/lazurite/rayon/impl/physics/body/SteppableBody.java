package dev.lazurite.rayon.impl.physics.body;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;

/**
 * All {@link PhysicsRigidBody} objects that implement this become
 * steppable by {@link MinecraftDynamicsWorld}.
 * @see MinecraftDynamicsWorld
 * @see EntityRigidBody
 */
public interface SteppableBody {
    void step(float delta);
}
