package dev.lazurite.rayon.impl.bullet.natives.fabric;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.natives.NativeLoader;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

/**
 * Fabric loader implementation of {@link NativeLoader}.
 */
public class NativeLoaderImpl {
    public static Path getSourceDir() {
        var path = FabricLoader.getInstance().getModContainer(Rayon.MODID).get().getRootPath();

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            path = Path.of(path.toString().replace("fabric", "common"));
        }

        return path;
    }

    public static Path getRunDir() {
        return FabricLoader.getInstance().getGameDir();
    }
}