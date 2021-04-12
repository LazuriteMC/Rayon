package dev.lazurite.rayon.core.impl;

import com.google.common.collect.Maps;
import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.util.BlockProperties;
import dev.lazurite.rayon.core.impl.util.supplier.world.ServerWorldSupplier;
import dev.lazurite.rayon.core.impl.physics.util.thread.ThreadStorage;
import dev.lazurite.rayon.core.impl.physics.util.NativeLoader;
import dev.lazurite.rayon.core.impl.physics.space.util.SpaceStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The common entrypoint for Rayon Core. Handles the server thread lifecycle,
 * {@link MinecraftSpace} creation, and bullet native loading
 * @see RayonCoreClient
 * @see NativeLoader
 */
public class RayonCoreCommon implements ModInitializer {
	public static final String MODID = "rayon-core";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Core");
	private static final Map<Identifier, BlockProperties> blockProps = Maps.newHashMap();

	@Override
	public void onInitialize() {
		NativeLoader.load();
		loadBlockProps();

		AtomicReference<PhysicsThread> thread = new AtomicReference<>();
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> thread.get().destroy());

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			thread.set(new PhysicsThread(server, Thread.currentThread(), new ServerWorldSupplier(server), "Server Physics Thread"));
			((ThreadStorage) server).setPhysicsThread(thread.get());
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (thread.get().throwable != null) {
				throw new RuntimeException(thread.get().throwable);
			}
		});

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			MinecraftSpace space = MinecraftSpace.get(world);
			space.step(space::canStep);
		});

		ServerWorldEvents.LOAD.register((server, world) -> {
			PhysicsSpaceEvents.PREINIT.invoker().onPreInit(thread.get(), world);
			((SpaceStorage) world).putSpace(MinecraftSpace.MAIN, new MinecraftSpace(thread.get(), world));
			PhysicsSpaceEvents.INIT.invoker().onInit(thread.get(), MinecraftSpace.get(world));
		});
	}

	public static Map<Identifier, BlockProperties> getBlockProps() {
		return blockProps;
	}

	public static void loadBlockProps() {
		FabricLoader.getInstance().getAllMods().forEach(mod -> {
			String modid = mod.getMetadata().getId();
			CustomValue rayon = mod.getMetadata().getCustomValue("rayon");

			if (rayon != null) {
				CustomValue blocks = rayon.getAsObject().get("blocks");

				if (blocks != null) {
					blocks.getAsArray().forEach(block -> {
						CustomValue name = block.getAsObject().get("name");

						if (name != null) {
							CustomValue friction = block.getAsObject().get("friction");
							CustomValue restitution = block.getAsObject().get("restitution");
							CustomValue collidable = block.getAsObject().get("collidable");

							blockProps.put(new Identifier(modid, name.getAsString()), new BlockProperties(
									friction == null ? -1.0f : (float) (double) friction.getAsNumber(),
									restitution == null ? -1.0f : (float) (double) restitution.getAsNumber(),
									collidable == null || collidable.getAsBoolean()
							));
						}
					});
				}
			}
		});
	}

	public static boolean isImmersivePortalsPresent() {
		return FabricLoader.getInstance().isModLoaded("immersive_portals");
	}
}