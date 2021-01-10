package dev.lazurite.rayon.util.config;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.helper.AirHelper;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;

/**
 * The POJO that hosts Rayon's Fiber config.
 * @see ConfigScreen
 */
@Settings(onlyAnnotated = true)
public class Config {
    public static final Config INSTANCE = new Config();
    public static final String CONFIG_NAME = "rayon.json";

    public boolean isRemote = false;
    public boolean debug = false;
    public boolean debugBlocks = false;

    @Setting
    public float gravity;

    @Setting
    @Setting.Constrain.Range(min = 1, max = 5)
    public int blockDistance;

    @Setting
    @Setting.Constrain.Range(min = 20, max = 260, step = 1.0f)
    public int stepRate;

    @Setting
    @Setting.Constrain.Range(min = 0.0f)
    public float airDensity;

    @Setting
    public AirHelper.Type airResistanceType;

    private Config() {
        this.gravity = -9.81f;
        this.blockDistance = 1;
        this.stepRate = 20;
        this.airDensity = 1.2f;
        this.airResistanceType = AirHelper.Type.SIMPLE;
    }

    public void load() {
        if (Files.exists(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME))) {
            try {
                FiberSerialization.deserialize(
                        ConfigTree.builder().applyFromPojo(INSTANCE, AnnotatedSettings.builder().build()).build(),
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
                    ConfigTree.builder().applyFromPojo(INSTANCE, AnnotatedSettings.builder().build()).build(),
                    Files.newOutputStream(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME)),
                    new JanksonValueSerializer(false)
            );
        } catch (IOException e) {
            Rayon.LOGGER.error("Error saving Rayon config.");
            e.printStackTrace();
        }
    }
}
