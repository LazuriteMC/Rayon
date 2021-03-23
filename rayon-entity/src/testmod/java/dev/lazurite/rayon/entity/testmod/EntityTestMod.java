package dev.lazurite.rayon.entity.testmod;

import dev.lazurite.rayon.core.api.event.ElementCollisionEvents;
import dev.lazurite.rayon.entity.testmod.common.entity.CubeEntity;
import dev.lazurite.rayon.entity.testmod.common.item.WandItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTestMod implements ModInitializer {
    public static final String MODID = "entitytestmod";
    public static final Logger LOGGER = LogManager.getLogger("Rayon Entity Test Mod");

    public static EntityType<CubeEntity> CUBE_ENTITY;
    public static WandItem WAND_ITEM;
    public static Block BLUE_BLOCK;

    @Override
    public void onInitialize() {
        CUBE_ENTITY = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(MODID, "cube_entity"),
                FabricEntityTypeBuilder.createLiving()
                        .entityFactory(CubeEntity::new)
                        .spawnGroup(SpawnGroup.MISC)
                        .defaultAttributes(LivingEntity::createLivingAttributes)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(80)
                        .build()
        );

        WAND_ITEM = Registry.register(
                Registry.ITEM,
                new Identifier(MODID, "wand_item"),
                new WandItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC)));

        BLUE_BLOCK = Registry.register(
                Registry.BLOCK,
                new Identifier(MODID, "blue_block"),
                new Block(FabricBlockSettings.of(Material.METAL).hardness(1.0f)));

        Registry.register(
                Registry.ITEM,
                new Identifier(MODID, "blue_block"),
                new BlockItem(BLUE_BLOCK, new Item.Settings().group(ItemGroup.MISC)));

        /* An example of a block collision event */
        ElementCollisionEvents.BLOCK_COLLISION.register((thread, element, block, impulse) -> {
            thread.execute(() -> {
                if (element instanceof CubeEntity) {
                    if (block.getBlockState().getBlock().equals(Blocks.BRICKS)) {
                        LOGGER.info("Touching bricks!!" + impulse);
                        ((CubeEntity) element).kill();
                    }
                }
            });
        });
    }
}
