package dev.lazurite.rayon;

import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;
import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.lazurite.rayon.util.NativeLoader;
import dev.lazurite.rayon.util.config.ConfigS2C;
import dev.lazurite.rayon.util.config.ConfigScreen;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.util.config.Config;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Rayon implements ModInitializer, ClientModInitializer, EntityComponentInitializer, WorldComponentInitializer, ModMenuApi {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	public static final ComponentKey<EntityRigidBody> DYNAMIC_BODY_ENTITY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "dynamic_body_entity"), EntityRigidBody.class);
	public static final ComponentKey<MinecraftDynamicsWorld> DYNAMICS_WORLD = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "dynamics_world"), MinecraftDynamicsWorld.class);

	@Override
	public void onInitialize() {
		LOGGER.info("Time to get physical!");
		Config.INSTANCE.load();
		NativeLoader.load();
	}

	@Override
	public void onInitializeClient() {
		RayonSpawnS2CPacket.register();
		ConfigS2C.register();
	}

	/**
	 * Registers every entity defined by other mods during initialization in CCA.
	 * @param registry the cardinal components entity registry
	 * @see EntityRigidBody
	 */
	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		DynamicEntityRegistry.INSTANCE.get().forEach(entry ->
				registry.registerFor(entry.getEntity(), DYNAMIC_BODY_ENTITY,
						(entity) -> EntityRigidBody.create(entity, entry.getShapeFactory(), entry.getMass(), entry.getDragCoefficient())));
	}

	/**
	 * Registers the {@link MinecraftDynamicsWorld} component in CCA.
	 * @param registry the cardinal components world registry
	 * @see MinecraftDynamicsWorld
	 */
	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(DYNAMICS_WORLD, MinecraftDynamicsWorld::new);
	}

	/**
	 * Adds the config screen mod menu.
	 * @return the {@link ConfigScreenFactory}
	 * @see ConfigScreen
	 */
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ConfigScreen::create;
	}
}
