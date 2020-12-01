package dev.lazurite.rayon.entity_example.server;

import dev.lazurite.rayon.entity_example.server.entity.TestEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * This is the server-side entrypoint for this mod. It normally
 * contains registries for things like entities, blocks, and items.
 * @author Ethan Johnson
 */
public class ServerInitializer implements ModInitializer {

    public static final String MODID = "rayon_entity_example";

    /** The new entity type for our {@link TestEntity} class. */
    public static EntityType<TestEntity> TEST_ENTITY;

    /**
     * Anything you want to run when the mod is loaded should go here.
     * In this case, we create our {@link EntityType<TestEntity>} object.
     */
    @Override
    public void onInitialize() {
        TEST_ENTITY = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(MODID, "test_entity"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, TestEntity::new)
                        .dimensions(EntityDimensions.changing(0.5F, 0.125F))
                        .trackRangeBlocks(80)
                        .trackedUpdateRate(3)
                        .build()
        );
    }
}
