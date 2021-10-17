package dev.lazurite.rayon.entity.testmod.common.entity;

import dev.lazurite.rayon.entity.testmod.EntityTestMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTestEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, EntityTestMod.MODID);

    public static final RegistryObject<EntityType<StoneBlockEntity>> STONE_BLOCK_ENTITY = ENTITIES.register(
            "stone_block_entity",
            () ->
                EntityType.Builder.of(StoneBlockEntity::new, MobCategory.MISC)
                        .setTrackingRange(60)
                        .sized(0.5F, 0.5F).build("stone_block_entity")
            );
}
