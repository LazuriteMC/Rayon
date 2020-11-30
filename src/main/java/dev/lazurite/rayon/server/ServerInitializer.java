package dev.lazurite.rayon.server;

import dev.lazurite.rayon.network.packet.PhysicsHandlerC2S;
import dev.lazurite.rayon.server.entity.TestEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ServerInitializer implements ModInitializer {
	public static final String MODID = "lazurite-physics";
	public static final String VERSION = "1.0.0";
	public static final String URL = "https://github.com/LazuriteMC/Lazurite-Physics-API/releases";

	public static EntityType<TestEntity> TEST_ENTITY;

	@Override
	public void onInitialize() {
		PhysicsHandlerC2S.register();

		TEST_ENTITY = Registry.register(
				Registry.ENTITY_TYPE,
				new Identifier(MODID, "test_entity"),
				FabricEntityTypeBuilder.create(SpawnGroup.MISC, TestEntity::new).dimensions(EntityDimensions.fixed(0.5F, 0.5F)).trackRangeBlocks(80).trackedUpdateRate(3).forceTrackedVelocityUpdates(true).build()
		);
	}
}
