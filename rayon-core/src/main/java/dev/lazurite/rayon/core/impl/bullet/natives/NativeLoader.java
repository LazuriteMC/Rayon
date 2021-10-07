package dev.lazurite.rayon.core.impl.bullet.natives;

import com.jme3.system.JmeSystem;
import com.jme3.system.NativeLibraryLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Facilitates copying of natives outside the jar so that LibBulletJME can load them.
 */
public class NativeLoader {
    public static void load() {
        final var fileName = getPlatformSpecificName();
        final var destination = FMLPaths.GAMEDIR.get().resolve("natives").resolve(fileName);
        final var destinationFile = destination.toFile();

        boolean delete = false;
        boolean copy = false;
        boolean[] load = new boolean[]{false};

        if (destinationFile.exists()) {
            try (
                    var fileInputStream = new FileInputStream(destinationFile);
                    var nativeStream = NativeLoader.class.getResourceAsStream("/assets/rayon-core/natives/" + fileName)
            ) {
                var destDigest = DigestUtils.md5Hex(fileInputStream);
                var jarDigest = DigestUtils.md5Hex(nativeStream);
                if(!destDigest.equals(jarDigest)){
                    delete = true;
                    copy = true;
                }else load[0] = true;

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to verify native libraries.");
            }
        } else {
            copy = true;
        }

        if(delete)destinationFile.delete();
        if(copy)copyNative(fileName, destination, load);
        if(load[0])NativeLibraryLoader.loadLibbulletjme(true, destinationFile, "Release", "Sp");
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

    private static void copyNative(String fileName, Path destination, boolean[] load){
        final var destinationFile = destination.toFile();

        destinationFile.getParentFile().mkdirs();
        try (
                var nativeStream = NativeLoader.class.getResourceAsStream("/assets/rayon-core/natives/" + fileName)
        ) {
            Files.copy(nativeStream, destination);
            load[0] = true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load bullet natives.");
        }
    }
}
