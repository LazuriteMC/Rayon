package dev.lazurite.rayon;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;
import dev.lazurite.rayon.impl.builder.RigidBodyEntry;
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
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Rayon implements ModInitializer, ClientModInitializer, EntityComponentInitializer, WorldComponentInitializer {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	public static final List<RigidBodyEntry<? extends Entity>> entries = Lists.newArrayList();

	public static final ComponentKey<EntityRigidBody> RIGID_BODY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "rigid_body"), EntityRigidBody.class);
	public static final ComponentKey<MinecraftDynamicsWorld> WORLD = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(MODID, "world"), MinecraftDynamicsWorld.class);

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
		entries.forEach(entry -> registry.registerFor(entry.getEntity(), RIGID_BODY,
			(entity) -> new EntityRigidBody(entity, entry.getShapeFactory(), entry.getMass(), entry.getDragCoefficient(), entry.getFriction(), entry.getRestitution())));
	}

	/**
	 * Registers the {@link MinecraftDynamicsWorld} component in CCA.
	 * @param registry the cardinal components world registry
	 * @see MinecraftDynamicsWorld
	 */
	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(WORLD, MinecraftDynamicsWorld::new);
	}
}
