package dev.lazurite.rayon.physics;

import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.lazurite.rayon.physics.entity.DynamicBodyEntity;
import dev.lazurite.rayon.physics.entity.StaticBodyEntity;
import dev.lazurite.rayon.physics.config.Config;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Rayon {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	public static final ComponentKey<DynamicBodyEntity> DYNAMIC_BODY_ENTITY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "dynamic_body_entity"), DynamicBodyEntity.class);
	public static final ComponentKey<StaticBodyEntity> STATIC_BODY_ENTITY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "static_body_entity"), StaticBodyEntity.class);
	public static final ComponentKey<MinecraftDynamicsWorld> DYNAMICS_WORLD = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "dynamics_world"), MinecraftDynamicsWorld.class);

	public void onInitServer() {
		LOGGER.info("Time to get physical!");
		Config.INSTANCE.load();
	}

	public void onInitClient() {

	}

	public void onInitEntityComponents(EntityComponentFactoryRegistry registry) {
		/* Every living entity has a static body */
		registry.registerFor(LivingEntity.class, STATIC_BODY_ENTITY, StaticBodyEntity::create);

		/* Every entity defined by mods during initialization has a dynamic body */
		DynamicEntityRegistry.INSTANCE.get().forEach(entry ->
			registry.registerFor(entry.getEntity(), DYNAMIC_BODY_ENTITY,
					(entity) -> DynamicBodyEntity.create(entity, entry.getShapeFactory(), entry.getMass())));
	}

	public void onInitWorldComponents(WorldComponentFactoryRegistry registry) {
		registry.register(DYNAMICS_WORLD, MinecraftDynamicsWorld::create);
	}
}
