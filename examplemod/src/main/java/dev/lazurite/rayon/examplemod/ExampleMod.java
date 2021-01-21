package dev.lazurite.rayon.examplemod;

import dev.lazurite.rayon.api.builder.RigidBodyBuilder;
import dev.lazurite.rayon.api.builder.RigidBodyRegistry;
import dev.lazurite.rayon.api.event.EntityBodyCollisionEvent;
import dev.lazurite.rayon.api.shape.provider.BoundingBoxShapeProvider;
import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import dev.lazurite.rayon.examplemod.item.WandItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleMod implements ModInitializer {
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
                        .dimensions(EntityDimensions.fixed(0.5f, 1.0f)) // (8/16 x 16/16)
                        .trackedUpdateRate(3)
                        .trackRangeBlocks(80)
                        .forceTrackedVelocityUpdates(true)
                        .build()
        );

        /* An example of using the builder and registering the built rigid body entry */
        RigidBodyRegistry.register(
                RigidBodyBuilder.create(RectangularPrismEntity.class, BoundingBoxShapeProvider::get)
                    .setMass(2.0f)
                    .setDrag(0.05f)
                    .setFriction(0.5f)
                    .setRestitution(0.75f)
                    .build());

        /* An example of a block collision event */
        EntityBodyCollisionEvent.BLOCK_COLLISION.register((entityBody, blockBody) -> {
            if (blockBody.getBlockState().getBlock().equals(Blocks.BRICKS)) {
                LOGGER.info("Touching bricks!!");
                entityBody.getEntity().kill();
            }
        });
    }
}
