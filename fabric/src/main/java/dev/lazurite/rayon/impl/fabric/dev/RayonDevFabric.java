package dev.lazurite.rayon.impl.fabric.dev;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.dev.RayonDev;
import dev.lazurite.rayon.impl.dev.entity.StoneBlockEntity;
import dev.lazurite.rayon.impl.dev.item.WandItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class RayonDevFabric {
    public static void init() {
        RayonDev.init();

        // Entity Registration
        RayonDev.STONE_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(Rayon.MODID, "stone_block_entity"),
                FabricEntityTypeBuilder.createLiving()
                        .entityFactory(StoneBlockEntity::new)
                        .spawnGroup(MobCategory.MISC)
                        .defaultAttributes(LivingEntity::createLivingAttributes)
                        .dimensions(EntityDimensions.fixed(0.75f, 0.25f))
                        .trackRangeBlocks(80)
                        .build()
        );

        // Item Registration
        RayonDev.WAND_ITEM = Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(Rayon.MODID, "wand_item"),
                new WandItem(new Item.Properties().stacksTo(1))
        );

        // Item group registration
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> {
            content.accept(RayonDev.WAND_ITEM);
        });
    }
}