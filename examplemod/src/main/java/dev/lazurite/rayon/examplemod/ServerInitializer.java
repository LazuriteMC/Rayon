package dev.lazurite.rayon.examplemod;

import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import dev.lazurite.rayon.examplemod.item.WandItem;
import dev.lazurite.rayon.physics.PhysicsWorld;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
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

    public static WandItem WAND_ITEM;
    public static EntityType<RectangularPrismEntity> RECTANGULAR_PRISM_ENTITY;

    @Override
    public void onInitialize() {
        PhysicsWorld.getInstance();

        WAND_ITEM = Registry.register(
                Registry.ITEM,
                new Identifier(MODID, "wand_item"),
                new WandItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC)));

//        RECTANGULAR_PRISM_ENTITY = Registry.register(
//                Registry.ENTITY_TYPE,
//                new Identifier(MODID, "rectangular_prism_entity"),
//                FabricEntityTypeBuilder.create(SpawnGroup.MISC, RectangularPrismEntity::new)
//                        .dimensions(EntityDimensions.changing(1.0f, 0.5f))
//                        .trackedUpdateRate(3)
//                        .trackRangeBlocks(80)
//                        .forceTrackedVelocityUpdates(true)
//                        .build()
//        );
    }
}
