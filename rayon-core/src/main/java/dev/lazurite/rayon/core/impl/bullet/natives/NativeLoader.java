package dev.lazurite.rayon.core.impl.bullet.natives;

import com.jme3.system.JmeSystem;
import com.jme3.system.NativeLibraryLoader;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

/**
 * Facilitates copying of natives outside the jar so that LibBulletJME can load them.
 */
public class NativeLoader {
    public static void load() {
        final var destination = FabricLoader.getInstance().getGameDir().resolve("natives");

        FabricLoader.getInstance().getModContainer("rayon-core").ifPresentOrElse(jar -> {
            final var natives = jar.getRootPath().resolve("assets").resolve("rayon-core").resolve("natives");
            final var fileName = getPlatformSpecificName();

            try {
                var destinationFile = destination.resolve(fileName);
                var originalFile = natives.resolve(fileName);

                if (!Files.exists(destination) && !Files.exists(destinationFile)) {
                    Files.createDirectory(destination);
                    Files.copy(originalFile, destinationFile);
                } else if (!Files.exists(destinationFile)) {
                    Files.copy(originalFile, destinationFile);
                } else {
                    var destinationHash = getChecksum(MessageDigest.getInstance("MD5"), destination.resolve(fileName).toFile());
                    var jarHash = getChecksum(MessageDigest.getInstance("MD5"), natives.resolve(fileName).toFile());

                    if (!jarHash.equals(destinationHash)) {
                        Files.delete(destinationFile);
                        Files.copy(originalFile, destinationFile);
                    }
                }

                // Load it!
                NativeLibraryLoader.loadLibbulletjme(true, destination.toFile(), "Release", "Sp");
            } catch (IOException | NoSuchElementException e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to load bullet natives.");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to verify native libraries.");
            }
        }, () -> {
            throw new RuntimeException("Rayon jar not found.");
        });
    }

    static String getChecksum(MessageDigest digest, File file) throws IOException {
        var inputStream = new FileInputStream(file);
        var byteArray = new byte[1024];
        var bytesCount = 0;

        while ((bytesCount = inputStream.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        inputStream.close();
        var bytes = digest.digest();
        var sb = new StringBuilder();

        for (var aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    static String getPlatformSpecificName() {
        final var platform = JmeSystem.getPlatform();

        var name = switch (platform) {
            case Windows32, Windows64 -> "bulletjme.dll";
            case Linux_ARM32, Linux_ARM64, Linux32, Linux64 -> "libbulletjme.so";
            case MacOSX32, MacOSX64 -> "libbulletjme.dylib";
            default -> throw new RuntimeException("Invalid platform " + platform);
        };

        return platform + "Release" + "Sp" + "_" + name;
    }
}
