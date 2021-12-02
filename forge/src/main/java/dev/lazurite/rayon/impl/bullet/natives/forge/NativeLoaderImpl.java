package dev.lazurite.rayon.impl.bullet.natives.forge;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.natives.NativeLoader;
import net.minecraftforge.fml.loading.FMLLoader;

import java.nio.file.Path;

/**
 * Forge mod loader implementation of {@link NativeLoader}.
 */
public class NativeLoaderImpl {
    public static Path getSourceDir() {
        var path = FMLLoader.getLoadingModList().getModFileById(Rayon.MODID).getFile().getFilePath();

        if (!FMLLoader.isProduction()) {
            path = Path.of(path.toString().replace("forge", "common"));
        }

        return path;
    }

    public static Path getRunDir() {
        return FMLLoader.getGamePath();
    }
}
