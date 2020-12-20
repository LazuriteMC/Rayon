package dev.lazurite.rayon.physics.helper;

import com.bulletphysics.dynamics.RigidBody;
import com.google.common.collect.Maps;
import dev.lazurite.rayon.physics.PhysicsWorld;
import dev.lazurite.rayon.util.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class EntityHelper {
    private final PhysicsWorld physicsWorld;
    private final Map<Entity, RigidBody> collisionEntities;

    public EntityHelper(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
        this.collisionEntities = Maps.newHashMap();
    }

    // TODO soft collisions
    public void load(Entity entity, ClientWorld world) {
        Box area = new Box(new BlockPos(entity.getPos())).expand(Constants.BLOCK_RADIUS);
        List<Entity> entityList = world.getOtherEntities(entity, area);

        entityList.forEach(otherEntity -> {

        });
    }
}
