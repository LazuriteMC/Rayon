package dev.lazurite.rayon.impl.util.ui;

import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.util.config.ConfigS2C;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

/**
 * Class for housing the method which returns a new config screen made using Cloth Config.
 * @see ModMenuEntry#getModConfigScreenFactory()
 */
public class ConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("config.rayon.title"))
                .setTransparentBackground(true)
                .setSavingRunnable(() -> {
                    Config.getInstance().save();

                    MinecraftClient client = MinecraftClient.getInstance();
                    IntegratedServer server = client.getServer();

                    if (server != null) {
                        server.getPlayerManager().getPlayerList().forEach(player -> {
                            if (!player.getUuid().equals(client.player.getUuid())) {
                                ConfigS2C.send(player, Config.getInstance());
                            }
                        });
                    }
                });

        return builder.setFallbackCategory(getPhysicsSettings(builder)).build();
    }

    public static ConfigCategory getPhysicsSettings(ConfigBuilder builder) {
        ConfigCategory category = builder.getOrCreateCategory(new TranslatableText("config.rayon.title"));

        /* Block Distance */
        category.addEntry(builder.entryBuilder().startIntSlider(
                new TranslatableText("config.rayon.option.block_distance"),
                Config.getInstance().getLocal().getBlockDistance(), 1, 5)
                .setDefaultValue(1)
                .setTooltip(
                        new TranslatableText("config.rayon.option.block_distance.tooltip"),
                        new TranslatableText("config.rayon.option.performance.high"))
                .setSaveConsumer(newValue -> Config.getInstance().getLocal().setBlockDistance(newValue))
                .build());

        /* Client Step Rate */
        category.addEntry(builder.entryBuilder().startIntSlider(
                new TranslatableText("config.rayon.option.step_rate"),
                Config.getInstance().getLocal().getStepRate(), 20, 260)
                .setDefaultValue(260)
                .setTooltip(
                        new TranslatableText("config.rayon.option.step_rate.tooltip"),
                        new TranslatableText("config.rayon.option.performance.medium"))
                .setTextGetter((currValue) -> currValue == 260 ? new TranslatableText("config.rayon.option.step_rate.max") : new LiteralText(String.valueOf(currValue)))
                .setSaveConsumer(newValue -> Config.getInstance().getLocal().setStepRate(newValue))
                .build());

        /* Simulation Max Sub Steps */
        category.addEntry(builder.entryBuilder().startIntSlider(
                new TranslatableText("config.rayon.option.max_sub_steps"),
                Config.getInstance().getLocal().getMaxSubSteps(), 5, 10)
                .setDefaultValue(5)
                .setTooltip(
                        new TranslatableText("config.rayon.option.max_sub_steps.tooltip"),
                        new TranslatableText("config.rayon.option.performance.low"))
                .setSaveConsumer(newValue -> Config.getInstance().getLocal().setMaxSubSteps(newValue))
                .build());

        if (!Config.getInstance().isRemote()) {
            /* Air Density */
            category.addEntry(builder.entryBuilder().startFloatField(
                    new TranslatableText("config.rayon.option.air_density"), Config.getInstance().getGlobal().getAirDensity())
                    .setDefaultValue(1.2f)
                    .setTooltip(
                            new TranslatableText("config.rayon.option.air_density.tooltip"),
                            new TranslatableText("config.rayon.option.performance.low"))
                    .setSaveConsumer(newValue -> Config.getInstance().getGlobal().setAirDensity(newValue))
                    .build());

            /* Gravity */
            category.addEntry(builder.entryBuilder().startFloatField(
                    new TranslatableText("config.rayon.option.gravity"), Config.getInstance().getGlobal().getGravity())
                    .setDefaultValue(-9.81f)
                    .setTooltip(
                            new TranslatableText("config.rayon.option.gravity.tooltip"),
                            new TranslatableText("config.rayon.option.performance.low"))
                    .setSaveConsumer(newValue -> Config.getInstance().getGlobal().setGravity(newValue))
                    .build());

            /* Air Resistance */
            category.addEntry(builder.entryBuilder().startBooleanToggle(
                    new TranslatableText("config.rayon.option.air_resistance_enabled"), Config.getInstance().getGlobal().isAirResistanceEnabled())
                    .setDefaultValue(true)
                    .setTooltip(
                            new TranslatableText("config.rayon.option.air_resistance_enabled.tooltip"),
                            new TranslatableText("config.rayon.option.performance.low"))
                    .setSaveConsumer(newValue -> Config.getInstance().getGlobal().setAirResistanceEnabled(newValue))
                    .build());
        }

        return category;
    }
}
