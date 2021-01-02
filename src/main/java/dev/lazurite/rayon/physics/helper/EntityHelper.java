package dev.lazurite.rayon.physics.helper;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.physics.entity.EntityRigidBody;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.function.Function;

public class EntityHelper {
    private final MinecraftDynamicsWorld dynamicsWorld;

    public EntityHelper(MinecraftDynamicsWorld dynamicsWorld) {
        this.dynamicsWorld = dynamicsWorld;
    }

    public void step(float delta) {
        for (Entity entity : getEntities()) {
            /* Step the component */
            EntityRigidBody.get(entity).step(delta);
        }
    }

    /**
     * Builds a list of every {@link Entity} in the player's world
     * that has a {@link EntityRigidBody} component attached to it.
     * @return the {@link List} of {@link Entity} objects
     * @see EntityHelper#getEntities(Function) 
     */
    public List<Entity> getEntities() {
        return getEntities(EntityRigidBody::get);
    }

    /**
     * Builds a list of every {@link Entity} in the player's world
     * that has a {@link EntityRigidBody} component (or any child
     * component) attached to it. The type of {@link EntityRigidBody}
     * can be determined using the {@link Function} argument.
     * @param func the {@link Function} used for filtering the types of {@link EntityRigidBody}
     * @return the {@link List} of {@link Entity} objects
     */
    public List<Entity> getEntities(Function<Entity, EntityRigidBody> func) {
        List<Entity> out = Lists.newArrayList();

        /* Client World */
        if (dynamicsWorld.getWorld().isClient()) {
            ((ClientWorld) dynamicsWorld.getWorld()).getEntities().forEach(entity -> {
                if (func.apply(entity) != null) {
                    out.add(entity);
                }
            });

        /* Server World */
        } else {
            ((ServerWorld) dynamicsWorld.getWorld()).entitiesByUuid.values().forEach(entity -> {
                if (func.apply(entity) != null) {
                    out.add(entity);
                }
            });
        }

        return out;
    }
}
