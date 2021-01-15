package dev.lazurite.rayon.util;

import com.jme3.system.NativeLibraryLoader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * A class for loading native bullet bindings. It handles downloading the necessary libraries from
 * online and storing them in the natives folder in the player's minecraft directory.
 */
public class NativeLoader {
    public enum OperatingSystem {
        WINDOWS("https://github.com/stephengold/Libbulletjme/releases/download/9.3.2/Windows64ReleaseSp_bulletjme.dll", "Windows64ReleaseSp_bulletjme.dll"),
        LINUX("https://github.com/stephengold/Libbulletjme/releases/download/9.3.2/Linux64ReleaseSp_libbulletjme.so", "Linux64ReleaseSp_libbulletjme.so");

        final String url;
        final String file;

        OperatingSystem(String url, String file) {
            this.url = url;
            this.file = file;
        }
    }

    public static void load() {
        String name = System.getProperty("os.name");
        OperatingSystem os;

        if (name.contains("Linux")) {
            os = OperatingSystem.LINUX;
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
