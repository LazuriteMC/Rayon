package dev.lazurite.rayon.examplemod;

import dev.lazurite.rayon.examplemod.item.WandItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * This is the server-side entrypoint for this mod. It normally
 * contains registries for things like entities, blocks, and items.
 * @author Ethan Johnson
 */
public class ServerInitializer implements ModInitializer {
    public static final String MODID = "examplemod";

    public static WandItem TEST_ITEM;

    @Override
    public void onInitialize() {
        TEST_ITEM = Registry.register(
                Registry.ITEM,
                new Identifier(MODID, "wand_item"),
                new WandItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC)));
    }
}
