package dev.lazurite.rayon;

import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;
import dev.lazurite.rayon.impl.builder.RigidBodyRegistryImpl;
import dev.lazurite.rayon.impl.util.NativeLoader;
import dev.lazurite.rayon.impl.util.config.ConfigS2C;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Rayon implements ModInitializer, ClientModInitializer, EntityComponentInitializer, WorldComponentInitializer {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	public static final ComponentKey<EntityRigidBody> DYNAMIC_BODY_ENTITY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "dynamic_body_entity"), EntityRigidBody.class);
	public static final ComponentKey<MinecraftDynamicsWorld> DYNAMICS_WORLD = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "dynamics_world"), MinecraftDynamicsWorld.class);

	@Override
	public void onInitialize() {
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
		RigidBodyRegistryImpl.INSTANCE.get().forEach(entry ->
				registry.registerFor(entry.getEntity(), DYNAMIC_BODY_ENTITY,
						(entity) -> new EntityRigidBody(
								entity,
								entry.getShapeFactory(),
								entry.getMass(),
								entry.getDragCoefficient(),
								entry.getFriction(),
								entry.getRestitution())));
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
}
