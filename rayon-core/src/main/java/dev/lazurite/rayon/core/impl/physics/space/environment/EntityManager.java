package dev.lazurite.rayon.core.impl.physics.space.environment;

import com.google.common.collect.Lists;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.EntityRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public final class EntityManager {
    private final List<Entity> toKeep = Lists.newArrayList();
    private final MinecraftSpace space;

    public EntityManager(MinecraftSpace space) {
        this.space = space;
    }

    public void tick() {
        space.getRigidBodiesByClass(ElementRigidBody.class).forEach(rigidBody -> {
            if (rigidBody.shouldDoEntityLoading()) {
                Vector3f pos = rigidBody.getPhysicsLocation(new Vector3f());

                if (load(new Box(new BlockPos(pos.x, pos.y, pos.z)).expand(rigidBody.getEnvironmentLoadDistance()))) {
                    space.getThread().execute(rigidBody::activate);
                }
            }
        });

        purge();
    }

    /**
     * Loads an individual element's entity area into the physics simulation. This
     * is also where each entity's {@link EntityRigidBody} object is instantiated
     * and properties such as position, rotation, etc. are applied here.
     * @param box the {@link Box} area around the element to search for entities within
     * @return whether or not to activate the rigid body
     */
    public boolean load(Box box) {
        List<Entity> entities = space.getWorld().getOtherEntities(null, box, EntityManager::canCollideWith);

        entities.forEach(entity -> {
            space.getThread().execute(() -> {
                EntityRigidBody rigidBody = findEntityRigidBody(space, entity);

                if (rigidBody == null) {
                    rigidBody = new EntityRigidBody(entity);

                    if (!space.getRigidBodyList().contains(rigidBody)) {
                        space.addCollisionObject(rigidBody);
                    }
                } else {
                    /* Update its position and rotation if it already exists */
                    rigidBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos().add(0, entity.getBoundingBox().getYLength() / 2.0, 0)));
                    rigidBody.setPhysicsRotation(QuaternionHelper.rotateY(new Quaternion(), -entity.yaw));
                    rigidBody.getDebugFrame().from(rigidBody.getDebugFrame(), rigidBody.getPhysicsLocation(new Vector3f()), rigidBody.getPhysicsRotation(new Quaternion()), rigidBody.getCollisionShape().boundingBox(new Vector3f(), new Quaternion(), new BoundingBox()));
                }
            });
        });

        toKeep.addAll(entities);
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
            if (!toKeep.contains(body.getEntity())) {
                toRemove.add(body);
            }
        });

        toRemove.forEach(space::removeCollisionObject);
        toKeep.clear();
    }

    public static EntityRigidBody findEntityRigidBody(MinecraftSpace space, Entity entity) {
        for (EntityRigidBody body : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            if (body.getEntity().equals(entity)) {
                return body;
            }
        }

        return null;
    }

    public static boolean canCollideWith(Entity entity) {
        return (entity instanceof BoatEntity || entity instanceof MinecartEntity || entity instanceof LivingEntity) && !(entity instanceof PhysicsElement);
    }
}
