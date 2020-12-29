package dev.lazurite.rayon.physics;

import dev.lazurite.rayon.api.DynamicEntityRegistry;
import dev.lazurite.rayon.physics.entity.DynamicPhysicsEntity;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Rayon {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	public static final ComponentKey<DynamicPhysicsEntity> PHYSICS_ENTITY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "entity"), DynamicPhysicsEntity.class);
	public static final ComponentKey<MinecraftDynamicsWorld> PHYSICS_WORLD = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "world"), MinecraftDynamicsWorld.class);

	public void onInitServer() {
		LOGGER.log(Level.INFO, "Time to get physical!");

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
		DynamicEntityRegistry.get().forEach(
				entity -> registry.registerFor(entity, PHYSICS_ENTITY, DynamicPhysicsEntity::new)
		);
	}

	public void onInitWorldComponents(WorldComponentFactoryRegistry registry) {
		registry.register(PHYSICS_WORLD, MinecraftDynamicsWorld::create);
	}
}
