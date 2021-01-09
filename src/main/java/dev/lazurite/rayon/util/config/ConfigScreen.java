package dev.lazurite.rayon.util.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class ConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("config.rayon.title"))
                .setTransparentBackground(true);

        ConfigCategory category = builder.getOrCreateCategory(new LiteralText("")); // category name is ignored

        category.addEntry(builder.entryBuilder().startIntSlider(
                new TranslatableText("config.rayon.option.block_distance"),
                Config.INSTANCE.blockDistance, 2, 6)
                .setDefaultValue(2)
                .setTooltip(new TranslatableText("config.rayon.option.block_distance.tooltip"))
                .setSaveConsumer(newValue -> Config.INSTANCE.blockDistance = newValue)
                .build());

        category.addEntry(builder.entryBuilder().startIntSlider(
                new TranslatableText("config.rayon.option.step_rate"),
                Config.INSTANCE.stepRate, 20, 260)
                .setDefaultValue(60)
                .setTooltip(new TranslatableText("config.rayon.option.step_rate.tooltip"))
                .setTextGetter((currValue) -> currValue == 260 ? new TranslatableText("config.rayon.option.step_rate.max") : new LiteralText(String.valueOf(currValue)))
                .setSaveConsumer(newValue -> Config.INSTANCE.stepRate = newValue)
                .build());

        category.addEntry(builder.entryBuilder().startFloatField(
                new TranslatableText("config.rayon.option.gravity"), Config.INSTANCE.gravity)
                .setDefaultValue(-9.81f)
                .setTooltip(new TranslatableText("config.rayon.option.gravity.tooltip"))
                .setSaveConsumer(newValue -> Config.INSTANCE.gravity = newValue)
                .build());

        return builder.setFallbackCategory(category).build();
    }
}
