package dev.lazurite.rayon.util.config;

import dev.lazurite.rayon.Rayon;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

/**
 * Class for housing the method which returns a new config screen made using Cloth Config.
 * @see Rayon#getModConfigScreenFactory()
 */
public class ConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("config.rayon.title"))
                .setTransparentBackground(true)
                .setSavingRunnable(() -> {
                    Config.INSTANCE.save();

                    MinecraftClient client = MinecraftClient.getInstance();
                    IntegratedServer server = client.getServer();

                    if (server != null) {
                        server.getPlayerManager().getPlayerList().forEach(player -> {
                            if (!player.getUuid().equals(client.player.getUuid())) {
                                ConfigS2C.send(player, Config.INSTANCE);
                            }
                        });
                    }
                });

        ConfigCategory category = builder.getOrCreateCategory(new LiteralText("")); // category name is ignored

        /* Block Distance */
        category.addEntry(builder.entryBuilder().startIntSlider(
                new TranslatableText("config.rayon.option.block_distance"),
                Config.INSTANCE.getLocal().getBlockDistance(), 1, 5)
                .setDefaultValue(1)
                .setTooltip(
                        new TranslatableText("config.rayon.option.block_distance.tooltip"),
                        new TranslatableText("config.rayon.option.block_distance.tooltip.performance"))
                .setSaveConsumer(newValue -> Config.INSTANCE.getLocal().setBlockDistance(newValue))
                .build());

        /* Client Step Rate */
        category.addEntry(builder.entryBuilder().startIntSlider(
                new TranslatableText("config.rayon.option.step_rate"),
                Config.INSTANCE.getLocal().getStepRate(), 20, 260)
                .setDefaultValue(20)
                .setTooltip(
                        new TranslatableText("config.rayon.option.step_rate.tooltip"),
                        new TranslatableText("config.rayon.option.step_rate.tooltip.performance"))
                .setTextGetter((currValue) -> currValue == 260 ? new TranslatableText("config.rayon.option.step_rate.max") : new LiteralText(String.valueOf(currValue)))
                .setSaveConsumer(newValue -> Config.INSTANCE.getLocal().setStepRate(newValue))
                .build());

        if (!Config.INSTANCE.isRemote()) {
            /* Air Density */
            category.addEntry(builder.entryBuilder().startFloatField(
                    new TranslatableText("config.rayon.option.air_density"), Config.INSTANCE.getGlobal().getAirDensity())
                    .setDefaultValue(1.2f)
                    .setTooltip(
                            new TranslatableText("config.rayon.option.air_density.tooltip"),
                            new TranslatableText("config.rayon.option.air_density.tooltip.performance"))
                    .setSaveConsumer(newValue -> Config.INSTANCE.getGlobal().setAirDensity(newValue))
                    .build());

            /* Gravity */
            category.addEntry(builder.entryBuilder().startFloatField(
                    new TranslatableText("config.rayon.option.gravity"), Config.INSTANCE.getGlobal().getGravity())
                    .setDefaultValue(-9.81f)
                    .setTooltip(
                            new TranslatableText("config.rayon.option.gravity.tooltip"),
                            new TranslatableText("config.rayon.option.gravity.tooltip.performance"))
                    .setSaveConsumer(newValue -> Config.INSTANCE.getGlobal().setGravity(newValue))
                    .build());
        }

        return builder.setFallbackCategory(category).build();
    }
}
