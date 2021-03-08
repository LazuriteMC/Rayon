package dev.lazurite.rayon.impl.bullet.world.environment;

import com.google.common.collect.Lists;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.body.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

import java.util.List;

public final class EntityManager {
    private final List<EntityRigidBody> toKeep = Lists.newArrayList();
    private final MinecraftSpace space;

    public EntityManager(MinecraftSpace space) {
        this.space = space;
    }

    /**
     * Loads an individual element's entity area into the physics simulation. This
     * is also where each entity's {@link EntityRigidBody} object is instantiated
     * and properties such as position, rotation, etc. are applied here.
     * @param box the {@link Box} area around the element to search for entities within
     */
    public void load(Box box) {
        space.getWorld().getEntitiesByClass(Entity.class, box, (entity -> !(entity instanceof PhysicsElement)))
                .forEach(entity -> {
                    EntityRigidBody rigidBody = findEntityRigidBody(entity);

                    if (rigidBody == null) {
                        rigidBody = new EntityRigidBody(entity);

                        if (!space.getRigidBodyList().contains(rigidBody)) {
                            space.addCollisionObject(rigidBody);
                        }
                    } else {
                        /* Update its position and rotation if it already exists */
                        rigidBody.setPhysicsLocation(VectorHelper.lerp(
                                new Vector3f((float) entity.prevX, (float) (entity.prevY + entity.getBoundingBox().getYLength() / 2.0), (float) entity.prevZ),
                                VectorHelper.vec3dToVector3f(entity.getPos().add(0, entity.getBoundingBox().getYLength() / 2.0, 0)),
                                0.6f));
                        rigidBody.setPhysicsRotation(QuaternionHelper.rotateY(new Quaternion(), -entity.yaw));
                    }

                    toKeep.add(rigidBody);
                });
    }

    /**
     * Prune out any unnecessary entities from the world during each call
     * to {@link MinecraftSpace#step}. The purpose is to prevent
     * any trailing or residual entities from being left over from a
     * previous {@link EntityManager#load} call.
     * <b>Note:</b> This method should only be called after every element
     * has been passed through the loading process. Otherwise, entities will
     * be removed from the simulation prematurely and cause you a headache.
     * @see EntityManager#load
     */
    public void purge() {
        List<EntityRigidBody> toRemove = Lists.newArrayList();

        space.getRigidBodiesByClass(EntityRigidBody.class).forEach(body -> {
            if (!toKeep.contains(body)) {
                toRemove.add(body);
            }
        });

        toRemove.forEach(space::removeCollisionObject);
        toKeep.clear();
    }

    public EntityRigidBody findEntityRigidBody(Entity entity) {
        for (EntityRigidBody body : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            if (body.getEntity().equals(entity)) {
                return body;
            }
        }

        return null;
    }
}
