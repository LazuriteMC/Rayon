package dev.lazurite.rayon.impl.physics.body;

import com.bulletphysics.dynamics.RigidBody;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;

/**
 * All {@link RigidBody} objects that implement this become
 * steppable by {@link MinecraftDynamicsWorld}.
 * @see MinecraftDynamicsWorld
 * @see EntityRigidBody
 */
public interface SteppableBody {
    void step(float delta);
}
