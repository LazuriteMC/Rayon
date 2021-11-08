package dev.lazurite.rayon.core.impl.bullet.natives.fabric;

import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.bullet.natives.NativeLoader;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

/**
 * Fabric loader implementation of {@link NativeLoader}.
 */
public class NativeLoaderImpl {
    public static Path getSourceDir() {
        var path = FabricLoader.getInstance().getModContainer(RayonCore.MODID).orElseThrow().getRootPath();

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            path = Path.of(path.toString().replace("fabric", "common"));
        }

        return path;
    }

    public static Path getRunDir() {
        return FabricLoader.getInstance().getGameDir();
    }
}