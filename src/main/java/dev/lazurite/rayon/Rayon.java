package dev.lazurite.rayon;

import dev.lazurite.rayon.component.DynamicPhysicsComponent;
import dev.lazurite.rayon.component.PhysicsComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Rayon {
	public static final Logger LOGGER = LogManager.getLogger("Rayon");
	public static final String MODID = "rayon";

	public static final ComponentKey<PhysicsComponent> PHYSICS =
			ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "physics"), PhysicsComponent.class);

	public void onInitServer() {

	}

	public void onInitClient() {

	}

	public void onInitComponent(EntityComponentFactoryRegistry registry) {
		registry.registerFor(CowEntity.class, PHYSICS, DynamicPhysicsComponent::new);
	}
}
