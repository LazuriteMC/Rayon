package dev.lazurite.rayon.impl.util;

import com.jme3.system.NativeLibraryLoader;
import dev.lazurite.rayon.impl.Rayon;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class NativeLoader {
    public static final String DEST_DIR = "natives/10.1.0/";
    public static final String NATIVE_ZIP = "natives.zip";
    public static final String INTERNAL_ZIP = "/assets/rayon/natives/natives.zip";

    public static void load() {
        File destination = new File(DEST_DIR);

        if (!destination.exists()) {
            try {
                destination.mkdirs();
                Files.copy(Rayon.class.getResourceAsStream(INTERNAL_ZIP), Paths.get(DEST_DIR + NATIVE_ZIP), StandardCopyOption.REPLACE_EXISTING);
                new ZipFile(DEST_DIR + NATIVE_ZIP).extractAll(DEST_DIR);
                Files.delete(Paths.get(DEST_DIR + NATIVE_ZIP));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to extract bullet natives.");
            }
        }

        NativeLibraryLoader.loadLibbulletjme(true, destination, "Release", "Sp");
    }
}
