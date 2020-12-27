package dev.lazurite.rayon.api.physics.helper;

import com.bulletphysics.dynamics.RigidBody;
import com.google.common.collect.Maps;
import dev.lazurite.rayon.api.physics.util.Constants;
import dev.lazurite.rayon.api.physics.world.MinecraftDynamicsWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class EntityHelper {
    private final MinecraftDynamicsWorld dynamicsWorld;
    private final Map<Entity, RigidBody> collisionEntities;

    public EntityHelper(MinecraftDynamicsWorld dynamicsWorld) {
        this.dynamicsWorld = dynamicsWorld;
        this.collisionEntities = Maps.newHashMap();
    }

    // TODO soft collisions
    public void load(Entity entity, World world) {
        Box area = new Box(new BlockPos(entity.getPos())).expand(Constants.BLOCK_RADIUS);
        List<Entity> entityList = world.getOtherEntities(entity, area);

        entityList.forEach(otherEntity -> {

        });
    }
}
