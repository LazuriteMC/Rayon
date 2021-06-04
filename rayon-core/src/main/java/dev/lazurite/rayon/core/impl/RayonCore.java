package dev.lazurite.rayon.core.impl;

import com.google.common.collect.Maps;
import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.util.supplier.world.ServerWorldSupplier;
import dev.lazurite.rayon.core.impl.util.storage.ThreadStorage;
import dev.lazurite.rayon.core.impl.util.NativeLoader;
import dev.lazurite.rayon.core.impl.util.storage.SpaceStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
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
public class RayonCore implements ModInitializer {
	public static final String MODID = "rayon-core";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Core");
	public static final Identifier MODDED_SERVER = new Identifier(RayonCore.MODID, "modded_server");
	private static final Map<Identifier, BlockProperties> blockProps = Maps.newHashMap();

	@Override
	public void onInitialize() {
		NativeLoader.load();
		loadBlockProps();

		var thread = new AtomicReference<PhysicsThread>();
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

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			sender.sendPacket(MODDED_SERVER, PacketByteBufs.create());
		});

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			var space = MinecraftSpace.get(world);
			space.step(space::canStep);
		});

		ServerWorldEvents.LOAD.register((server, world) -> {
			PhysicsSpaceEvents.PREINIT.invoker().onPreInit(thread.get(), world);
			((SpaceStorage) world).setSpace(new MinecraftSpace(thread.get(), world));
			PhysicsSpaceEvents.INIT.invoker().onInit(thread.get(), MinecraftSpace.get(world));
		});
	}

	public static Map<Identifier, BlockProperties> getBlockProps() {
		return blockProps;
	}

	public static void loadBlockProps() {
		FabricLoader.getInstance().getAllMods().forEach(mod -> {
			var modid = mod.getMetadata().getId();
			var rayon = mod.getMetadata().getCustomValue("rayon");

			if (rayon != null) {
				var blocks = rayon.getAsObject().get("blocks");

				if (blocks != null) {
					blocks.getAsArray().forEach(block -> {
						var name = block.getAsObject().get("name");

						if (name != null) {
							var friction = block.getAsObject().get("friction");
							var restitution = block.getAsObject().get("restitution");
							var collidable = block.getAsObject().get("collidable");

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

	public record BlockProperties(float friction, float restitution, boolean collidable) { }
}