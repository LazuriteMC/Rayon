package dev.lazurite.rayon.physics;

import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.lazurite.rayon.physics.util.config.ConfigScreen;
import dev.lazurite.rayon.physics.entity.DynamicBodyEntity;
import dev.lazurite.rayon.physics.entity.StaticBodyEntity;
import dev.lazurite.rayon.physics.util.config.Config;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Rayon implements ModInitializer, EntityComponentInitializer, WorldComponentInitializer, ModMenuApi {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	public static final ComponentKey<DynamicBodyEntity> DYNAMIC_BODY_ENTITY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "dynamic_body_entity"), DynamicBodyEntity.class);
	public static final ComponentKey<StaticBodyEntity> STATIC_BODY_ENTITY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "static_body_entity"), StaticBodyEntity.class);
	public static final ComponentKey<MinecraftDynamicsWorld> DYNAMICS_WORLD = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "dynamics_world"), MinecraftDynamicsWorld.class);

	@Override
	public void onInitialize() {
		LOGGER.info("Time to get physical!");
		Config.INSTANCE.load();
	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		/* Every living entity has a static body */
		registry.registerFor(LivingEntity.class, STATIC_BODY_ENTITY, StaticBodyEntity::create);

		/* Every entity defined by mods during initialization has a dynamic body */
		DynamicEntityRegistry.INSTANCE.get().forEach(entry ->
				registry.registerFor(entry.getEntity(), DYNAMIC_BODY_ENTITY,
						(entity) -> DynamicBodyEntity.create(entity, entry.getShapeFactory(), entry.getMass())));
	}

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(DYNAMICS_WORLD, MinecraftDynamicsWorld::create);
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ConfigScreen::new;
	}
}
