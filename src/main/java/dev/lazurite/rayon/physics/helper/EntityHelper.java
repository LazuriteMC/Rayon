package dev.lazurite.rayon.physics.helper;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.physics.util.Constants;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class EntityHelper {
    private final MinecraftDynamicsWorld dynamicsWorld;

    public EntityHelper(MinecraftDynamicsWorld dynamicsWorld) {
        this.dynamicsWorld = dynamicsWorld;
    }

    // TODO soft collisions
    public void load() {
        World world = dynamicsWorld.getWorld();
        Box area = new Box(new BlockPos(entity.getPos())).expand(Constants.BLOCK_RADIUS);
        List<Entity> entityList = world.getOtherEntities(entity, area);

        entityList.forEach(otherEntity -> {

        });
    }

    public List<Entity> getEntities() {
        return Lists.newArrayList(collisionEntities.keySet());
    }
}
