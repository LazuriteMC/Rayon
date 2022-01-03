package dev.lazurite.rayon.impl;

import dev.lazurite.rayon.impl.bullet.natives.NativeLoader;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.event.ClientEventHandler;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Rayon {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	private static final Map<Block, BlockProperty> blockProperties = new ConcurrentHashMap<>();

	public static void init() {
		NativeLoader.load();
		ServerEventHandler.register();

		Rayon.addBlockProperty(Blocks.ICE, 0.05f, 0.25f, true);
		Rayon.addBlockProperty(Blocks.SLIME_BLOCK, 3.0f, 3.0f, true);
		Rayon.addBlockProperty(Blocks.HONEY_BLOCK, 3.0f, 0.25f, true);
		Rayon.addBlockProperty(Blocks.SOUL_SAND, 3.0f, 0.25f, true);
	}

	public static void initClient() {
		ClientEventHandler.register();
	}

	public static PhysicsThread getThread(boolean isClient) {
		return isClient ? ClientEventHandler.getThread() : ServerEventHandler.getThread();
	}

	public static void addBlockProperty(Block block, float friction, float restitution, boolean collidable) {
		blockProperties.put(block, new BlockProperty(Math.max(friction, 0.0f), Math.max(restitution, 0.0f), collidable));
	}

	public static BlockProperty getBlockProperty(Block block) {
		return blockProperties.get(block);
	}

	public record BlockProperty (float friction, float restitution, boolean collidable) { }
}