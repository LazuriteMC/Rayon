package dev.lazurite.rayon.util;

import com.jme3.system.NativeLibraryLoader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

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

    private OperatingSystem os;

    public NativeLoader() {
        String os = System.getProperty("os.name");

        if (os.contains("Windows")) {
            this.os = OperatingSystem.WINDOWS;
        } else if (os.contains("Linux")) {
            this.os = OperatingSystem.LINUX;
        }

        getFromWeb();
        NativeLibraryLoader.loadLibbulletjme(true, new File("native/"), "Release", "Sp");
    }

    public void getFromWeb() {
        try {
            File file = new File("native/" + os.file);

            if (!file.exists()) {
                BufferedInputStream in = new BufferedInputStream(new URL(os.url).openStream());
                File nativeDirectory = new File("native/");

                if (!nativeDirectory.exists()) {
                    nativeDirectory.mkdir();
                }

                FileOutputStream out = new FileOutputStream("native/" + os.file);
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
