package dev.lazurite.rayon.core.impl.bullet.natives;

import com.jme3.system.JmeSystem;
import com.jme3.system.NativeLibraryLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Facilitates copying of natives outside the jar so that LibBulletJME can load them.
 */
public class NativeLoader {
    public static void load() {
        final var destination = FMLPaths.GAMEDIR.get().resolve("natives");

        final var fileName = getPlatformSpecificName();

        try {
            var destinationFile = destination.resolve(fileName);

            if (!Files.exists(destinationFile)) {
                if(!Files.exists(destination)){
                    Files.createDirectory(destination);
                }
                Files.copy(
                        Objects.requireNonNull(
                                NativeLoader.class.getResourceAsStream("/assets/rayon-core/natives/" + fileName)
                        ), destinationFile
                );
            } else {
                InputStream destinationInputStream = new FileInputStream(destinationFile.toFile());
                var destinationHash = getChecksum(MessageDigest.getInstance("MD5"), destination.resolve(fileName));
                var jarHash = getChecksum(MessageDigest.getInstance("MD5"), natives.resolve(fileName));

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

    static String getChecksum(MessageDigest digest, InputStream input) throws IOException {
        var channel = Files.newByteChannel(path, StandardOpenOption.READ);
        var buf = ByteBuffer.allocate(1024);
        var bytesRead = 0;

        while ((bytesRead = channel.read(buf)) > 0) {
            buf.flip();
            digest.update(buf.array(), 0, bytesRead);
            buf.clear();
        }

        channel.close();
        var bytes = digest.digest();
        var sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
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
