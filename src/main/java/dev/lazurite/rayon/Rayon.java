package dev.lazurite.rayon;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.component.DynamicPhysicsComponent;
import dev.lazurite.rayon.component.PhysicsComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Rayon {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");
	public static final ComponentKey<PhysicsComponent> PHYSICS = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "physics"), PhysicsComponent.class);

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

	public void onInitComponent(EntityComponentFactoryRegistry registry) {
		getEntityContainer().forEach(
				value -> registry.registerFor(value, PHYSICS, DynamicPhysicsComponent::new)
		);
	}
}
