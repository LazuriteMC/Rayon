package dev.lazurite.rayon.examplemod;

import dev.lazurite.rayon.api.DynamicEntityRegistry;
import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import dev.lazurite.rayon.examplemod.item.WandItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleMod implements ModInitializer {
    public static final String MODID = "examplemod";
    public static final Logger LOGGER = LogManager.getLogger("Rayon");

    public static WandItem WAND_ITEM;
    public static EntityType<RectangularPrismEntity> RECTANGULAR_PRISM_ENTITY;

    @Override
    public void onInitialize() {
        LOGGER.log(Level.INFO, "TEST MESSAGE FROM EXAMPLE MOD");
        WAND_ITEM = Registry.register(
                Registry.ITEM,
                new Identifier(MODID, "wand_item"),
                new WandItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC)));

        RECTANGULAR_PRISM_ENTITY = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(MODID, "rectangular_prism_entity"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, RectangularPrismEntity::new)
                        .dimensions(EntityDimensions.changing(1.0f, 0.5f))
                        .trackedUpdateRate(3)
                        .trackRangeBlocks(80)
                        .forceTrackedVelocityUpdates(true)
                        .build()
        );

        DynamicEntityRegistry.register(RectangularPrismEntity.class);
    }

//    @Override
//    public void onInitializeClient() {
//        System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBB");
//        EntityRendererRegistry.INSTANCE.register(ExampleMod.RECTANGULAR_PRISM_ENTITY, (entityRenderDispatcher, context) -> new RectangularPrismEntityRenderer(entityRenderDispatcher));
//    }
}
