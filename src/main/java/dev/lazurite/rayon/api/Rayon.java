package dev.lazurite.rayon.api;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.api.physics.entity.DynamicEntityPhysics;
import dev.lazurite.rayon.api.physics.entity.PhysicsEntityComponent;
import dev.lazurite.rayon.api.physics.world.MinecraftDynamicsWorld;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Rayon {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	public static final ComponentKey<PhysicsEntityComponent> PHYSICS_ENTITY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "physics_entity"), PhysicsEntityComponent.class);
	public static final ComponentKey<MinecraftDynamicsWorld> PHYSICS_WORLD = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "physics_world"), MinecraftDynamicsWorld.class);

	private static final List<Class<? extends Entity>> entityContainer = Lists.newArrayList();

	public static List<Class<? extends Entity>> getEntityContainer() {
		return entityContainer;
	}

	public static void register(Class<? extends Entity> entity) {
		getEntityContainer().add(entity);
	}

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
		getEntityContainer().forEach(
				entity -> registry.registerFor(entity, PHYSICS_ENTITY, DynamicEntityPhysics::new)
		);
	}

	public void onInitWorldComponents(WorldComponentFactoryRegistry registry) {
		registry.register(PHYSICS_WORLD, MinecraftDynamicsWorld::create);
	}
}
