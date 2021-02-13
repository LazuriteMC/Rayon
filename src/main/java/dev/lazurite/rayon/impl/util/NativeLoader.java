package dev.lazurite.rayon.impl.util;

import com.jme3.system.NativeLibraryLoader;
import dev.lazurite.rayon.impl.Rayon;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * A class for loading native bullet bindings. It handles downloading the necessary libraries from
 * online and storing them in the natives folder in the player's minecraft directory.
 * @see Rayon#onInitialize()
 */
public class NativeLoader {
    public static final String VERSION = "10.0.0";

    public enum OperatingSystem {
        WINDOWS(String.format("https://github.com/stephengold/Libbulletjme/releases/download/%s/Windows64ReleaseSp_bulletjme.dll", VERSION), "Windows64ReleaseSp_bulletjme.dll"),
        LINUX(String.format("https://github.com/stephengold/Libbulletjme/releases/download/%s/Linux64ReleaseSp_libbulletjme.so", VERSION), "Linux64ReleaseSp_libbulletjme.so"),
        MACOS(String.format("https://github.com/stephengold/Libbulletjme/releases/download/%s/MacOSX64ReleaseSp_libbulletjme.dylib", VERSION), "MacOSX64ReleaseSp_libbulletjme.dylib");

        final String url;
        final String file;

        OperatingSystem(String url, String file) {
            this.url = url;
            this.file = file;
        }
    }

    public static void load() {
        String name = System.getProperty("os.name");
        Rayon.LOGGER.info("Downloading natives for " + name + "...");
        OperatingSystem os;

        if (name.contains("Linux")) {
            os = OperatingSystem.LINUX;
        } else if (name.contains("Mac")) {
            os = OperatingSystem.MACOS;
        } else {
            os = OperatingSystem.WINDOWS;
        }

        getFromWeb(os);
        NativeLibraryLoader.loadLibbulletjme(true, new File("natives/"), "Release", "Sp");
    }

    public static void getFromWeb(OperatingSystem os) {
        try {
            File file = new File("natives/" + os.file);

            if (!file.exists()) {
                BufferedInputStream in = new BufferedInputStream(new URL(os.url).openStream());
                File nativeDirectory = new File("natives/");

                if (!nativeDirectory.exists()) {
                    nativeDirectory.mkdir();
                }

                FileOutputStream out = new FileOutputStream("natives/" + os.file);
                byte[] dataBuffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    out.write(dataBuffer, 0, bytesRead);
                }

                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
