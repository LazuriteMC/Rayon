package dev.lazurite.rayon.entity_example.server;

import dev.lazurite.rayon.entity_example.server.entity.TestEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ServerInitializer implements ModInitializer {
    public static final String MODID = "rayon_entity_example";

    public static EntityType<TestEntity> TEST_ENTITY;

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
