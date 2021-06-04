package dev.lazurite.rayon.core.impl.util;

import com.jme3.system.NativeLibraryLoader;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * This class copies and then loads native files stored within the rayon jar file.
 * Upon first launch, rayon will make a copy of the native library files within the
 * .minecraft/natives/[version] folder.
 */
public class NativeLoader {
    public static final Path DEST_DIR = Paths.get("natives", "10.1.0");

    public static void load() {
        Path destination = FabricLoader.getInstance().getGameDir().normalize().resolve(DEST_DIR);

        try {
            Path source = FabricLoader.getInstance().getModContainer("rayon-core")
                    .get().getRootPath().resolve("assets").resolve("rayon-core").resolve("natives");

            for (Path path : Files.walk(source).collect(Collectors.toList())) {
                if (!Files.isDirectory(path)) {
                    Path d = destination.resolve(source.relativize(path).getFileName().toString());

                    if (!Files.exists(d)) {
                        if (!Files.exists(d.getParent())) {
                            Files.createDirectories(d.getParent());
                        }

                        Files.copy(path, d);
                    }
                }
            }
        } catch (IOException | NoSuchElementException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to extract bullet natives.");
        }

        NativeLibraryLoader.loadLibbulletjme(true, destination.toFile(), "Release", "Sp");
    }
}
