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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.nio.file.Files;

@Settings(onlyAnnotated = true)
public class Config {
    public static final Config INSTANCE = new Config();
    public static final String CONFIG_NAME = "rayon.json";

    @Setting
    public float gravity;

    @Setting
    @Setting.Constrain.Range(min = 2, max = 6)
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
        this.blockDistance = 2;
        this.stepRate = 60;
        this.airDensity = 1.2f;
        this.airResistanceType = AirHelper.Type.SIMPLE;
    }

    public void send(PacketByteBuf buf) {
        buf.writeFloat(gravity);
        buf.writeInt(blockDistance);
        buf.writeInt(stepRate);
        buf.writeFloat(airDensity);
        buf.writeEnumConstant(airResistanceType);
    }

    @Environment(EnvType.CLIENT)
    public void receive(PacketByteBuf buf) {
        this.gravity = buf.readFloat();
        this.blockDistance = buf.readInt();
        this.stepRate = buf.readInt();
        this.airDensity = buf.readFloat();
        this.airResistanceType = buf.readEnumConstant(AirHelper.Type.class);
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
