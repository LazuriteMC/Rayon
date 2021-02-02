package dev.lazurite.rayon.impl.util.ui;

import dev.lazurite.rayon.impl.physics.manager.DebugManager;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.util.config.ConfigS2C;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

/**
 * Handles creation of the config screen and also
 * the process of opening it within mod menu.
 */
@Environment(EnvType.CLIENT)
public class ConfigScreen implements ModMenuApi {
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
                            if (!player.equals(client.player)) {
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

        /* Debug Render Distance */
        category.addEntry(builder.entryBuilder().startIntSlider(
                new TranslatableText("config.rayon.option.debug_distance"),
                Config.getInstance().getLocal().getDebugDistance(), 3, 32)
                .setDefaultValue(10)
                .setTooltip(
                        new TranslatableText("config.rayon.option.debug_distance.tooltip"),
                        new TranslatableText("config.rayon.option.performance.high"))
                .setSaveConsumer(newValue -> Config.getInstance().getLocal().setDebugDistance(newValue))
                .build());

        /* Debug Draw Mode */
        category.addEntry(builder.entryBuilder().startEnumSelector(
                new TranslatableText("config.rayon.option.debug_draw_mode"),
                DebugManager.DrawMode.class,
                Config.getInstance().getLocal().getDebugDrawMode())
                .setDefaultValue(DebugManager.DrawMode.LINES)
                .setTooltip(
                        new TranslatableText("config.rayon.option.debug_draw_mode.tooltip"),
                        new TranslatableText("config.rayon.option.performance.low"))
                .setEnumNameProvider((value) -> new TranslatableText(((DebugManager.DrawMode) value).getTranslation()))
                .setSaveConsumer(newValue -> Config.getInstance().getLocal().setDebugDrawMode(newValue))
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

    /**
     * Adds the config screen to mod menu.
     * @return the {@link ConfigScreenFactory}
     */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::create;
    }
}
