package dev.lazurite.rayon.entity.testmod;

import dev.lazurite.rayon.core.api.event.collision.ElementCollisionEvents;
import dev.lazurite.rayon.entity.testmod.common.entity.StoneBlockEntity;
import dev.lazurite.rayon.entity.testmod.common.item.WandItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTestMod implements ModInitializer {
    public static final String MODID = "rayon-entity-testmod";
    public static final Logger LOGGER = LogManager.getLogger("Rayon Entity Test Mod");

    public static EntityType<StoneBlockEntity> STONE_BLOCK_ENTITY;
    public static WandItem WAND_ITEM;
    public static Block BLUE_DIRT;

    @Override
    public void onInitialize() {
        STONE_BLOCK_ENTITY = Registry.register(
                Registry.ENTITY_TYPE,
                new ResourceLocation(MODID, "stone_block_entity"),
                FabricEntityTypeBuilder.createLiving()
                        .entityFactory(StoneBlockEntity::new)
                        .spawnGroup(MobCategory.AMBIENT.MISC)
                        .defaultAttributes(LivingEntity::createLivingAttributes)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(80)
                        .build()
        );

        WAND_ITEM = Registry.register(
                Registry.ITEM,
                new ResourceLocation(MODID, "wand_item"),
                new WandItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));

        BLUE_DIRT = Registry.register(
                Registry.BLOCK,
                new ResourceLocation(MODID, "blue_dirt"),
                new Block(FabricBlockSettings.of(Material.METAL).hardness(1.0f)));

        Registry.register(
                Registry.ITEM,
                new ResourceLocation(MODID, "blue_dirt"),
                new BlockItem(BLUE_DIRT, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

        /* An example of a block collision event */
        ElementCollisionEvents.TERRAIN_COLLISION.register((element, terrainObject, impulse) -> {
            if (element instanceof StoneBlockEntity) {
                terrainObject.getBlockState().ifPresent(blockState -> {
                    if (blockState.getBlock().equals(Blocks.BRICKS)) {
                        LOGGER.info("Touching bricks!!" + impulse);
                        ((StoneBlockEntity) element).kill();
                    }
                });
            }
        });
    }
}
