package dev.lazurite.rayon.api;

import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityRigidBody;
import net.minecraft.world.entity.Entity;

/**
 * Use this interface to create a physics entity.
 * @see PhysicsElement
 */
public interface EntityPhysicsElement extends PhysicsElement<Entity> {
    @Override
    EntityRigidBody getRigidBody();

    @Override
    default MinecraftShape genShape() {
        final var box = cast().getBoundingBox();
        return MinecraftShape.of(box.contract(box.getXsize() * 0.25, box.getYsize() * 0.25, box.getZsize() * 0.25));
    }
}
