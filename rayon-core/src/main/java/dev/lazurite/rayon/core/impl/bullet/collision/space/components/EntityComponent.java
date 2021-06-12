package dev.lazurite.rayon.core.impl.bullet.collision.space.components;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.collision.body.MinecraftRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.BoxHelper;
import dev.lazurite.rayon.core.impl.bullet.math.VectorHelper;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.entity.EntitySupplier;

public class EntityComponent implements MinecraftSpace.Component {
    @Override
    public void apply(MinecraftSpace space) {
        for (var rigidBody : space.getRigidBodiesByClass(MinecraftRigidBody.class)) {
            var box = rigidBody.boundingBox(new BoundingBox());
            var location = rigidBody.getPhysicsLocation(new Vector3f()).subtract(new Vector3f(0, -box.getYExtent(), 0));
            var mass = rigidBody.getMass();

            for (var entity : EntitySupplier.getInsideOf(rigidBody)) {
                var entityPos = VectorHelper.vec3dToVector3f(entity.getPos().add(0, entity.getBoundingBox().getYLength(), 0));
                var normal = location.subtract(entityPos).multLocal(new Vector3f(1, 0, 1)).normalize();

                var intersection = entity.getBoundingBox().intersection(BoxHelper.bulletToMinecraft(box));
                var force = normal.clone().multLocal((float) intersection.getAverageSideLength() / (float) BoxHelper.bulletToMinecraft(box).getAverageSideLength())
                        .multLocal(mass).multLocal(new Vector3f(1, 0, 1));
                rigidBody.applyCentralImpulse(force);
            }
        }
    }
}
