package dev.lazurite.rayon.physics;

import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.lazurite.rayon.physics.entity.DynamicPhysicsEntity;
import dev.lazurite.rayon.physics.entity.EntityRigidBody;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Rayon {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	public static final ComponentKey<EntityRigidBody> PHYSICS_ENTITY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "entity"), EntityRigidBody.class);
	public static final ComponentKey<MinecraftDynamicsWorld> PHYSICS_WORLD = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "world"), MinecraftDynamicsWorld.class);

	public void onInitServer() {
		LOGGER.info("Time to get physical!");

//		FabricLoader.getInstance().getAllMods().forEach(mod -> {
//			CustomValue value = mod.getMetadata().getCustomValue("blocks");
//
//			if (value != null) {
//
//			}
//		});
	}

	public void onInitClient() {

	}

	public void onInitEntityComponents(EntityComponentFactoryRegistry registry) {
		DynamicEntityRegistry.INSTANCE.get().forEach(entry ->
			registry.registerFor(entry.getEntity(), PHYSICS_ENTITY,
					(entity) -> DynamicPhysicsEntity.create(entity, entry.getShapeFactory(), entry.getMass())));
	}

	public void onInitWorldComponents(WorldComponentFactoryRegistry registry) {
		registry.register(PHYSICS_WORLD, MinecraftDynamicsWorld::create);
	}
}
