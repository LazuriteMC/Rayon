package dev.lazurite.rayon.api;

import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import net.minecraft.entity.Entity;

public interface PhysicsElement {
    void step(MinecraftSpace space);

    ElementRigidBody getRigidBody();

    default Entity asEntity() {
        return (Entity) this;
    }
}
