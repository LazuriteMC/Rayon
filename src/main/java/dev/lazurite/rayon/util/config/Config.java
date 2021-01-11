package dev.lazurite.rayon.util.config;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.util.config.settings.GlobalSettings;
import dev.lazurite.rayon.util.config.settings.LocalSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;

/**
 * The class containing two POJOs that hosts Rayon's Fiber config.
 * @see ConfigScreen
 */
public final class Config {
    public static final Config INSTANCE = new Config();
    public static final String CONFIG_NAME = "rayon.json";

    public boolean debug = false;
    public boolean debugBlocks = false;

    private final LocalSettings local = new LocalSettings(1, 20);
    private final GlobalSettings global = new GlobalSettings(-9.81f, 1.2f);
    private GlobalSettings remoteGlobal;

    private Config() {
    }

    public void setRemoteGlobal(GlobalSettings remoteGlobal) {
        this.remoteGlobal = remoteGlobal;
    }

    public GlobalSettings getGlobal() {
        return isRemote() ? remoteGlobal : global;
    }

    public LocalSettings getLocal() {
        return local;
    }

    public boolean isRemote() {
        return this.remoteGlobal != null;
    }

    public void load() {
        if (Files.exists(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME))) {
            try {
                FiberSerialization.deserialize(
                        ConfigTree.builder()
                                .applyFromPojo(getLocal(), AnnotatedSettings.builder().build())
                                .applyFromPojo(getGlobal(), AnnotatedSettings.builder().build())
                                .build(),
                        Files.newInputStream(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME)),
                        new JanksonValueSerializer(false)
                );
            } catch (IOException | FiberException e) {
                Rayon.LOGGER.error("Error loading Rayon config.");
                e.printStackTrace();
            }
        } else {
            Rayon.LOGGER.info("Creating Rayon config.");
            this.save();
        }
    }

    public void save() {
        try {
            FiberSerialization.serialize(
                    ConfigTree.builder()
                            .applyFromPojo(getLocal(), AnnotatedSettings.builder().build())
                            .applyFromPojo(getGlobal(), AnnotatedSettings.builder().build())
                            .build(),
                    Files.newOutputStream(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME)),
                    new JanksonValueSerializer(false)
            );
        } catch (IOException e) {
            Rayon.LOGGER.error("Error saving Rayon config.");
            e.printStackTrace();
        }
    }
}
