package dev.lazurite.rayon.impl.bullet.collision.space.supplier.entity;

import com.jme3.bounding.BoundingBox;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;

import java.util.ArrayList;
import java.util.List;

public interface EntitySupplier {
    static List<Entity> getInsideOf(ElementRigidBody rigidBody) {
        if (!rigidBody.isInWorld()) {
            return new ArrayList<>();
        }

        final var space = rigidBody.getSpace();
        final var box = Convert.toMinecraft(rigidBody.boundingBox(new BoundingBox()));

        return rigidBody.getSpace().getLevel().getEntitiesOfClass(Entity.class, box,
                entity -> (entity instanceof Boat || entity instanceof Minecart || entity instanceof LivingEntity) && !(entity instanceof PhysicsElement));
    }
}
