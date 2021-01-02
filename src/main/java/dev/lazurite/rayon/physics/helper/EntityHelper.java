package dev.lazurite.rayon.physics.helper;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.physics.entity.EntityRigidBody;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class EntityHelper {
    private final MinecraftDynamicsWorld dynamicsWorld;

    public EntityHelper(MinecraftDynamicsWorld dynamicsWorld) {
        this.dynamicsWorld = dynamicsWorld;
    }

    public void step(float delta) {
        for (Entity entity : getEntities()) {
            EntityRigidBody physics = EntityRigidBody.get(entity);

            if (physics != null) {
                if (!dynamicsWorld.getCollisionObjectArray().contains(physics)) {
                    dynamicsWorld.addRigidBody(physics);
                }

                physics.step(delta);
            }
        }
    }

    public List<Entity> getEntities() {
        List<Entity> out = Lists.newArrayList();

        if (dynamicsWorld.getWorld().isClient()) {
            ((ClientWorld) dynamicsWorld.getWorld()).getEntities().forEach(entity -> {
                if (EntityRigidBody.get(entity) != null) {
                    out.add(entity);
                }
            });
        } else {
            ((ServerWorld) dynamicsWorld.getWorld()).entitiesByUuid.values().forEach(entity -> {
                if (EntityRigidBody.get(entity) != null) {
                    out.add(entity);
                }
            });
        }

        return out;
    }
}
