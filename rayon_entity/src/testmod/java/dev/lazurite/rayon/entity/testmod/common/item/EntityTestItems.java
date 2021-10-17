package dev.lazurite.rayon.entity.testmod.common.item;

import dev.lazurite.rayon.entity.testmod.EntityTestMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTestItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EntityTestMod.MODID);

    public static final RegistryObject<Item> WAND_ITEM = ITEMS.register(
            "wand_item",
            () -> new WandItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC))
    );

    public static final RegistryObject<BlockItem> BLUE_BLOCK = ITEMS.register(
            "blue_dirt",
            () -> new BlockItem(EntityTestMod.BLUE_DIRT, new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );
}
