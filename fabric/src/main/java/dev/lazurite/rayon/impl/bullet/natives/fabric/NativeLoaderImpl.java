package dev.lazurite.rayon.impl.bullet.natives.fabric;

import dev.lazurite.rayon.impl.bullet.natives.NativeLoader;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

/**
 * Fabric loader implementation of {@link NativeLoader}.
 */
public class NativeLoaderImpl {
    public static Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }
}