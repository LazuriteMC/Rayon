package dev.lazurite.rayon.impl.dev;

import dev.lazurite.rayon.api.event.collision.ElementCollisionEvents;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.dev.entity.StoneBlockEntity;
import dev.lazurite.rayon.impl.dev.item.WandItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;

public class RayonDev {
    public static EntityType<StoneBlockEntity> STONE_BLOCK_ENTITY;
    public static WandItem WAND_ITEM;

    public static void init() {
        /* An example of a block collision event */
        ElementCollisionEvents.BLOCK_COLLISION.register((element, terrainObject, impulse) -> {
            if (element instanceof StoneBlockEntity) {
                if (terrainObject.getBlockState().getBlock().equals(Blocks.BRICKS)) {
                    Rayon.LOGGER.info("Touching bricks!!" + impulse);
                    ((StoneBlockEntity) element).kill();
                }
            }
        });
    }
}