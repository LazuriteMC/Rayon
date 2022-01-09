package dev.lazurite.rayon.test.fabric;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.test.RayonTest;
import dev.lazurite.rayon.test.common.entity.StoneBlockEntity;
import dev.lazurite.rayon.test.common.item.WandItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class RayonTestFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RayonTest.init();

        // Entity Registration
        RayonTest.STONE_BLOCK_ENTITY = Registry.register(
                Registry.ENTITY_TYPE,
                new ResourceLocation(RayonTest.MODID, "stone_block_entity"),
                FabricEntityTypeBuilder.createLiving()
                        .entityFactory(StoneBlockEntity::new)
                        .spawnGroup(MobCategory.MISC)
                        .defaultAttributes(LivingEntity::createLivingAttributes)
                        .dimensions(EntityDimensions.fixed(0.375f, 0.125f))
                        .trackRangeBlocks(80)
                        .build()
        );

        // Item Registration
        RayonTest.WAND_ITEM = Registry.register(
                Registry.ITEM,
                new ResourceLocation(RayonTest.MODID, "wand_item"),
                new WandItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));

        // Block Registration
        RayonTest.BLUE_DIRT = Registry.register(
                Registry.BLOCK,
                new ResourceLocation(RayonTest.MODID, "blue_dirt"),
                new Block(FabricBlockSettings.of(Material.METAL).hardness(1.0f)));

        Registry.register(
                Registry.ITEM,
                new ResourceLocation(RayonTest.MODID, "blue_dirt"),
                new BlockItem(RayonTest.BLUE_DIRT, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

        Rayon.addBlockProperty(RayonTest.BLUE_DIRT, 1.0f, 2.0f, true, true);
    }
}