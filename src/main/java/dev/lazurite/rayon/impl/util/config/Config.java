package dev.lazurite.rayon.impl.util.config;

import dev.lazurite.rayon.impl.physics.manager.DebugManager;
import dev.lazurite.rayon.impl.util.config.settings.GlobalSettings;
import dev.lazurite.rayon.impl.util.config.settings.LocalSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;

/**
 * The class containing two POJOs that hosts Rayon's Fiber config.
 * @see ConfigScreen
 */
public final class Config {
    private static final Config instance = new Config();
    public static final String CONFIG_NAME = "rayon.json";

    private final LocalSettings local = new LocalSettings(1, 100, 10, DebugManager.DrawMode.LINES);
    private final GlobalSettings global = new GlobalSettings(-9.81f, 1.2f, true);
    private GlobalSettings remoteGlobal;

    private Config() {
    }

    public static Config getInstance() {
        return instance;
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
        return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT) && remoteGlobal != null;
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
                e.printStackTrace();
            }
        } else {
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
            e.printStackTrace();
        }
    }
}
