package dev.lazurite.rayon.core.impl.util.supplier.entity;

import com.jme3.bounding.BoundingBox;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.BoxHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EntitySupplier {
    static List<Entity> getInsideOf(ElementRigidBody rigidBody) {
        if (!rigidBody.isInWorld()) {
            return new ArrayList<>(); // no entities 4 u
        }

        MinecraftSpace space = rigidBody.getSpace();
        PhysicsThread thread = space.getThread();

        if (!thread.getParentThread().equals(Thread.currentThread())) {
            return CompletableFuture.supplyAsync(() -> getInsideOf(rigidBody), thread.getParentExecutor()).join();
        } else {
            Box box = BoxHelper.bulletToMinecraft(rigidBody.boundingBox(new BoundingBox()));
            return rigidBody.getSpace().getWorld().getEntitiesByClass(Entity.class, box, ElementRigidBody::canCollideWith);
        }
    }
}
