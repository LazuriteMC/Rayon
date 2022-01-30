package dev.lazurite.rayon.test.forge;

import dev.lazurite.rayon.impl.bullet.collision.space.block.BlockProperty;
import dev.lazurite.rayon.test.RayonTest;
import dev.lazurite.rayon.test.common.entity.StoneBlockEntity;
import dev.lazurite.rayon.test.common.item.WandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(RayonTest.MODID)
public class RayonTestForge {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RayonTest.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RayonTest.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, RayonTest.MODID);

    public static final RegistryObject<Block> BLUE_DIRT = BLOCKS.register("blue_dirt", () -> new Block(Block.Properties.of(Material.STONE)));

    public static final RegistryObject<Item> WAND_ITEM = ITEMS.register("wand_item", () -> new WandItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> BLUE_DIRT_ITEM = ITEMS.register("blue_dirt", () -> new BlockItem(BLUE_DIRT.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistryObject<EntityType<? extends LivingEntity>> STONE_BLOCK_ENTITY = ENTITIES.register("stone_block_entity",
            () -> EntityType.Builder.of(StoneBlockEntity::new, MobCategory.MISC)
                    .sized(0.75f, 0.25f)
                    .setTrackingRange(80)
                    .build(new ResourceLocation(RayonTest.MODID, "stone_block_entity").toString()));

    public RayonTestForge() {
        RayonTest.init();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void onInitialize(FMLCommonSetupEvent event) {
        RayonTest.BLUE_DIRT = BLUE_DIRT.get();
        RayonTest.WAND_ITEM = (WandItem) WAND_ITEM.get();
        RayonTest.STONE_BLOCK_ENTITY = (EntityType<StoneBlockEntity>) STONE_BLOCK_ENTITY.get();

        BlockProperty.addBlockProperty(RayonTest.BLUE_DIRT, 1.0f, 2.0f, true, true);
    }

    @SubscribeEvent
    public void onRegisterAttributes(EntityAttributeCreationEvent event) {
        event.put(STONE_BLOCK_ENTITY.get(), LivingEntity.createLivingAttributes().build());
    }
}