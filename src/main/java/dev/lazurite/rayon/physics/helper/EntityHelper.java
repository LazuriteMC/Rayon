package dev.lazurite.rayon.physics.helper;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.physics.entity.DynamicPhysicsEntity;
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
            DynamicPhysicsEntity physics = DynamicPhysicsEntity.get(entity);

            if (physics != null) {
                if (!dynamicsWorld.getCollisionObjectArray().contains(physics.getRigidBody())) {
                    dynamicsWorld.addRigidBody(physics.getRigidBody());
                }

                physics.step(delta);
            }
        }
    }

    public List<Entity> getEntities() {
        List<Entity> out = Lists.newArrayList();

        if (dynamicsWorld.getWorld().isClient()) {
            ((ClientWorld) dynamicsWorld.getWorld()).getEntities().forEach(entity -> {
                if (DynamicPhysicsEntity.get(entity) != null) {
                    out.add(entity);
                }
            });
        } else {
            ((ServerWorld) dynamicsWorld.getWorld()).entitiesByUuid.values().forEach(entity -> {
                if (DynamicPhysicsEntity.get(entity) != null) {
                    out.add(entity);
                }
            });
        }

        return out;
    }
}
