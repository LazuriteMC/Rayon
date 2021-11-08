package dev.lazurite.rayon.core.impl.bullet.natives.forge;

import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.bullet.natives.NativeLoader;
import net.minecraftforge.fml.loading.FMLLoader;

import java.nio.file.Path;

/**
 * Forge mod loader implementation of {@link NativeLoader}.
 */
public class NativeLoaderImpl {
    public static Path getSourceDir() {
        var path = FMLLoader.getLoadingModList().getModFileById(RayonCore.MODID).getFile().getFilePath();

        if (!FMLLoader.isProduction()) {
            path = Path.of(path.toString().replace("forge", "common"));
        }

        return path;
    }

    public static Path getRunDir() {
        return FMLLoader.getGamePath();
    }
}
