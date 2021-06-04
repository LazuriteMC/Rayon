package dev.lazurite.rayon.core.impl.util.supplier.entity;

import com.jme3.bounding.BoundingBox;
import dev.lazurite.rayon.core.impl.physics.space.body.MinecraftRigidBody;
import dev.lazurite.rayon.core.impl.util.math.BoxHelper;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EntitySupplier {
    static List<Entity> getInsideOf(MinecraftRigidBody rigidBody) {
        if (!rigidBody.isInWorld()) {
            return new ArrayList<>();
        }

        final var space = rigidBody.getSpace();
        final var thread = space.getWorkerThread();

        if (!thread.getParentThread().equals(Thread.currentThread())) {
            return CompletableFuture.supplyAsync(() -> getInsideOf(rigidBody), thread.getParentExecutor()).join();
        } else {
            var box = BoxHelper.bulletToMinecraft(rigidBody.boundingBox(new BoundingBox()));
            return rigidBody.getSpace().getWorld().getEntitiesByClass(Entity.class, box, MinecraftRigidBody::canCollideWith);
        }
    }
}
