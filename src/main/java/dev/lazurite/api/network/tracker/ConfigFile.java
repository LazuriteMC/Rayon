package dev.lazurite.api.network.tracker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;

@Environment(EnvType.CLIENT)
public class ConfigFile {
    public static Config readConfig(String filename) {
        Config config = new Config();

        try {
            config.load(new FileInputStream("config/" + filename));
        } catch (IOException e) {
            System.err.println("Error reading " + filename);
            e.printStackTrace();

            try {
                copyNewFile(filename);
                return readConfig(filename);
            } catch (IOException f) {
                f.printStackTrace();
            }
        }

        return config;
    }

    public static void writeConfig(Config config, String filename) {
        try {
            config.store(new FileOutputStream("config/" + filename), "");
        } catch (IOException e) {
            System.err.println("Error reading " + filename);
            e.printStackTrace();
        }
    }

    public static void copyNewFile(String filename) throws IOException {
        InputStream in = Config.class.getResourceAsStream("/" + filename);
        OutputStream out = new FileOutputStream("config/" + filename);
        IOUtils.copy(in, out);
    }
}
