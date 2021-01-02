package dev.lazurite.rayon.examplemod;

import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import dev.lazurite.rayon.examplemod.item.WandItem;
import dev.lazurite.rayon.examplemod.render.RectangularPrismEntityRenderer;
import dev.lazurite.rayon.api.shape.EntityBoundingBoxShape;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleMod implements ModInitializer, ClientModInitializer {
    public static final String MODID = "examplemod";
    public static final Logger LOGGER = LogManager.getLogger("Rayon Example Mod");

    public static WandItem WAND_ITEM;
    public static EntityType<RectangularPrismEntity> RECTANGULAR_PRISM_ENTITY;

    @Override
    public void onInitialize() {
        WAND_ITEM = Registry.register(
                Registry.ITEM,
                new Identifier(MODID, "wand_item"),
                new WandItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC)));

        RECTANGULAR_PRISM_ENTITY = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(MODID, "rectangular_prism_entity"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, RectangularPrismEntity::new)
                        .dimensions(EntityDimensions.changing(0.5f, 1.5f))
                        .trackedUpdateRate(3)
                        .trackRangeBlocks(80)
                        .forceTrackedVelocityUpdates(true)
                        .build()
        );

        DynamicEntityRegistry.INSTANCE.register(RectangularPrismEntity.class, EntityBoundingBoxShape::new, 1.0f);
    }

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(ExampleMod.RECTANGULAR_PRISM_ENTITY, (entityRenderDispatcher, context) -> new RectangularPrismEntityRenderer(entityRenderDispatcher));
    }
}
