package dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.entity;

import com.jme3.bounding.BoundingBox;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EntitySupplier {
    static List<Entity> getInsideOf(ElementRigidBody rigidBody) {
        if (!rigidBody.isInWorld()) {
            return new ArrayList<>();
        }

        final var space = rigidBody.getSpace();
        final var thread = space.getWorkerThread();

        if (!thread.getParentThread().equals(Thread.currentThread())) {
            return CompletableFuture.supplyAsync(() -> getInsideOf(rigidBody), thread.getParentExecutor()).join();
        } else {
            var box = Convert.toMinecraft(rigidBody.boundingBox(new BoundingBox()));
            return rigidBody.getSpace().getWorld().getEntitiesByClass(Entity.class, box,
                    entity -> (entity instanceof BoatEntity || entity instanceof MinecartEntity || entity instanceof LivingEntity) && !(entity instanceof PhysicsElement));
        }
    }
}
