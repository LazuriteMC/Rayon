package dev.lazurite.rayon.impl.forge.dev;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.dev.RayonDev;
import dev.lazurite.rayon.impl.dev.entity.StoneBlockEntity;
import dev.lazurite.rayon.impl.dev.item.WandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RayonDevForge {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Rayon.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Rayon.MODID);
    public static final RegistryObject<Item> WAND_ITEM = ITEMS.register("wand_item", () -> new WandItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<EntityType<? extends LivingEntity>> STONE_BLOCK_ENTITY = ENTITIES.register("stone_block_entity",
            () -> EntityType.Builder.of(StoneBlockEntity::new, MobCategory.MISC)
                    .sized(0.75f, 0.25f)
                    .setTrackingRange(80)
                    .build(new ResourceLocation(Rayon.MODID, "stone_block_entity").toString()));

    public static void init() {
        RayonDev.init();
        FMLJavaModLoadingContext.get().getModEventBus().register(RayonDevForge.class);
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void onInitialize(FMLCommonSetupEvent event) {
        RayonDev.WAND_ITEM = (WandItem) WAND_ITEM.get();
        RayonDev.STONE_BLOCK_ENTITY = (EntityType<StoneBlockEntity>) STONE_BLOCK_ENTITY.get();
    }

    @SubscribeEvent
    public void onRegisterAttributes(EntityAttributeCreationEvent event) {
        event.put(STONE_BLOCK_ENTITY.get(), LivingEntity.createLivingAttributes().build());
    }
}