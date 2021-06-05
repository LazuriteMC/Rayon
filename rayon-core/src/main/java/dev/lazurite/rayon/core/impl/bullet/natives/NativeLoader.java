package dev.lazurite.rayon.core.impl.bullet.natives;

import com.jme3.system.JmeSystem;
import com.jme3.system.NativeLibraryLoader;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;

/**
 * Facilitates copying of natives outside the jar so that LibBulletJME can load them.
 */
public class NativeLoader {
    public static void load() {
        var destination = FabricLoader.getInstance().getGameDir().resolve("natives");

        FabricLoader.getInstance().getModContainer("rayon-core").ifPresentOrElse(jar -> {
            var natives = jar.getRootPath().resolve("assets").resolve("rayon-core").resolve("natives");

            try {
                // Delete the old natives/ directory if it's still there
                if (Files.exists(destination)) {
                    FileUtils.deleteDirectory(destination.toFile());
                }

                // Create the temporary natives/ folder
                Files.createDirectory(destination);

                // Copy the specific native lib to the natives/ folder
                var fileName = getPlatformSpecificName();
                Files.copy(natives.resolve(fileName), destination.resolve(fileName));

                // Load it!
                NativeLibraryLoader.loadLibbulletjme(true, destination.toFile(), "Release", "Sp");
            } catch (IOException | NoSuchElementException e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to load bullet natives.");
            }
        }, () -> {
            throw new RuntimeException("Rayon jar not found.");
        });
    }

    static String getPlatformSpecificName() {
        var platform = JmeSystem.getPlatform();

        var name = switch (platform) {
            case Windows32, Windows64 -> "bulletjme.dll";
            case Linux_ARM32, Linux_ARM64, Linux32, Linux64 -> "libbulletjme.so";
            case MacOSX32, MacOSX64 -> "libbulletjme.dylib";
            default -> throw new RuntimeException("Invalid platform " + platform);
        };

        return platform + "Release" + "Sp" + "_" + name;
    }
}
