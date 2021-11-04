package dev.lazurite.rayon.entity.api;

import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import net.minecraft.world.entity.Entity;

/**
 * Use this interface to create a physics entity.
 * @see PhysicsElement
 */
public interface EntityPhysicsElement extends PhysicsElement {
    @Override EntityRigidBody getRigidBody();

    @Override
    default MinecraftShape genShape() {
        final var box = asEntity().getBoundingBox();
        return MinecraftShape.of(box.contract(box.getXsize() * 0.25, box.getYsize() * 0.25, box.getZsize() * 0.25));
    }

    default Entity asEntity() {
        return (Entity) this;
    }
}
