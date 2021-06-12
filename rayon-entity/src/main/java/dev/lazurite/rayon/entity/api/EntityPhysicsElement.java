package dev.lazurite.rayon.entity.api;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import net.minecraft.entity.Entity;

public interface EntityPhysicsElement extends PhysicsElement {
    @Override EntityRigidBody getRigidBody();

    @Override
    default MinecraftShape genShape() {
        var box = asEntity().getBoundingBox();
        box = box.contract(box.getXLength() * 0.1, box.getYLength() * 0.1, box.getZLength() * 0.1);
        return MinecraftShape.of(box);
    }

    default Entity asEntity() {
        return (Entity) this;
    }
}
