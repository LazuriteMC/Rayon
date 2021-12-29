package dev.lazurite.rayon.impl.bullet.natives.forge;

import dev.lazurite.rayon.impl.bullet.natives.NativeLoader;
import net.minecraftforge.fml.loading.FMLLoader;

import java.nio.file.Path;

/**
 * Forge mod loader implementation of {@link NativeLoader}.
 */
public class NativeLoaderImpl {
    public static Path getGameDir() {
        return FMLLoader.getGamePath();
    }
}
