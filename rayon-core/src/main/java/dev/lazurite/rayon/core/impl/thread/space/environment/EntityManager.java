package dev.lazurite.rayon.core.impl.thread.space.environment;

import com.google.common.collect.Lists;
import com.jme3.math.Quaternion;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.thread.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.thread.space.body.EntityRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
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
     * @return whether or not to activate the rigid body
     */
    public boolean load(Box box) {
        List<Entity> entities = space.getWorld().getEntitiesByClass(Entity.class, box, (entity -> !(entity instanceof PhysicsElement)));

        entities.forEach(entity -> {
            EntityRigidBody rigidBody = findEntityRigidBody(entity);

            if (rigidBody == null) {
                rigidBody = new EntityRigidBody(entity);

                if (!space.getRigidBodyList().contains(rigidBody)) {
                    space.addCollisionObject(rigidBody);
                }
            } else {
                /* Update its position and rotation if it already exists */
                rigidBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos().add(0, entity.getBoundingBox().getYLength() / 2.0, 0)));
                rigidBody.setPhysicsRotation(QuaternionHelper.rotateY(new Quaternion(), -entity.yaw));
            }

            toKeep.add(rigidBody);
        });

        return !entities.isEmpty();
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
