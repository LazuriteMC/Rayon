package dev.lazurite.rayon.physics.helper;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity;
import dev.lazurite.rayon.physics.body.entity.StaticBodyEntity;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

import javax.vecmath.Vector3f;
import java.util.List;

public class EntityHelper {
    private final MinecraftDynamicsWorld dynamicsWorld;
    private final List<StaticBodyEntity> toKeep;

    public EntityHelper(MinecraftDynamicsWorld dynamicsWorld) {
        this.dynamicsWorld = dynamicsWorld;
        this.toKeep = Lists.newArrayList();
    }

    public void load(List<DynamicBodyEntity> dynamicBodyEntities, Box area) {
        dynamicBodyEntities.forEach(body -> load(body, area.offset(VectorHelper.vector3fToVec3d(body.getCenterOfMassPosition(new Vector3f())))));
        purge();
    }

    private void load(DynamicBodyEntity dynamicBody, Box area) {
        dynamicsWorld.getWorld().getOtherEntities(dynamicBody.getEntity(), area,
                entity -> entity instanceof LivingEntity).forEach(
                entity -> {
                    StaticBodyEntity staticBody = StaticBodyEntity.create(entity);
                    toKeep.add(staticBody);

                    if (!dynamicsWorld.getCollisionObjectArray().contains(staticBody)) {
                        dynamicsWorld.addRigidBody(staticBody);
                    }
                });
    }

    public void purge() {
        List<StaticBodyEntity> toRemove = Lists.newArrayList();

        dynamicsWorld.getCollisionObjectArray().forEach(body -> {
            if (body instanceof StaticBodyEntity && !toKeep.contains(body)) {
                toRemove.add((StaticBodyEntity) body);
            }
        });

        toRemove.forEach(dynamicsWorld::removeRigidBody);
        toKeep.clear();
    }
}
