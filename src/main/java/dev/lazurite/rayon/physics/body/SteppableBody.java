package dev.lazurite.rayon.physics.body;

import com.bulletphysics.dynamics.RigidBody;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity;

/**
 * All {@link RigidBody} objects that implement this become
 * steppable by {@link MinecraftDynamicsWorld}.
 * @see MinecraftDynamicsWorld
 * @see DynamicBodyEntity
 */
public interface SteppableBody {
    void step(float delta);
}
