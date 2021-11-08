package dev.lazurite.rayon.core.impl.bullet.natives;

import com.jme3.system.JmeSystem;
import com.jme3.system.NativeLibraryLoader;
import dev.architectury.injectables.annotations.ExpectPlatform;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

/**
 * Facilitates copying of natives outside the jar so that LibBulletJME can load them.
 */
public class NativeLoader {
    public static void load() {
        final var source = getSourceDir();
        final var destination = getRunDir();

        final var natives = source.resolve("assets/natives");
        final var fileName = getPlatformSpecificName();

        try {
            final var destinationFile = destination.resolve(fileName);
            final var originalFile = natives.resolve(fileName);

            if (!Files.exists(destination) && !Files.exists(destinationFile)) {
                Files.createDirectory(destination);
                Files.copy(originalFile, destinationFile);
            } else if (!Files.exists(destinationFile)) {
                Files.copy(originalFile, destinationFile);
            } else {
                final var destinationHash = getChecksum(MessageDigest.getInstance("MD5"), destination.resolve(fileName));
                final var jarHash = getChecksum(MessageDigest.getInstance("MD5"), natives.resolve(fileName));

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
    }

    @ExpectPlatform
    static Path getSourceDir() {
        throw new AssertionError();
    }

    @ExpectPlatform
    static Path getRunDir() {
        throw new AssertionError();
    }

    /**
     * Magic.
     * @param digest
     * @param path
     * @return
     * @throws IOException
     */
    static String getChecksum(MessageDigest digest, Path path) throws IOException {
        final var channel = Files.newByteChannel(path, StandardOpenOption.READ);
        final var buf = ByteBuffer.allocate(1024);
        var bytesRead = 0;

        while ((bytesRead = channel.read(buf)) > 0) {
            buf.flip();
            digest.update(buf.array(), 0, bytesRead);
            buf.clear();
        }

        channel.close();
        final var bytes = digest.digest();
        final var sb = new StringBuilder();

        for (var b : bytes) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    static String getPlatformSpecificName() {
        final var platform = JmeSystem.getPlatform();

        final var name = switch (platform) {
            case Windows32, Windows64 -> "bulletjme.dll";
            case Linux_ARM32, Linux_ARM64, Linux32, Linux64 -> "libbulletjme.so";
            case MacOSX32, MacOSX64 -> "libbulletjme.dylib";
            default -> throw new RuntimeException("Invalid platform " + platform);
        };

        return platform + "Release" + "Sp" + "_" + name;
    }
}