package dev.lazurite.rayon.test;

import dev.lazurite.rayon.api.event.collision.ElementCollisionEvents;
import dev.lazurite.rayon.test.common.entity.StoneBlockEntity;
import dev.lazurite.rayon.test.common.item.WandItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RayonTest {
    public static final String MODID = "rayon_test";
    public static final Logger LOGGER = LogManager.getLogger("Rayon Test");

    public static EntityType<StoneBlockEntity> STONE_BLOCK_ENTITY;
    public static WandItem WAND_ITEM;
    public static Block BLUE_DIRT;

    public static void init() {

        /* An example of a block collision event */
        ElementCollisionEvents.BLOCK_COLLISION.register((element, terrainObject, impulse) -> {
            if (element instanceof StoneBlockEntity) {
                if (terrainObject.getBlockState().getBlock().equals(Blocks.BRICKS)) {
                    LOGGER.info("Touching bricks!!" + impulse);
                    ((StoneBlockEntity) element).kill();
                }
            }
        });
    }
}