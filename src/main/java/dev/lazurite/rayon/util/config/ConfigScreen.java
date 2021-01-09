package dev.lazurite.rayon.util.config;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.helper.AirHelper;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

/**
 * Simply returns a newly built config screen made using Cloth Config.
 * @see Rayon#getModConfigScreenFactory()
 */
public class ConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("config.rayon.title"))
                .setTransparentBackground(true)
                .setSavingRunnable(Config.INSTANCE::save);

        ConfigCategory category = builder.getOrCreateCategory(new LiteralText("")); // category name is ignored

        /* Block Distance */
        category.addEntry(builder.entryBuilder().startIntSlider(
                new TranslatableText("config.rayon.option.block_distance"),
                Config.INSTANCE.blockDistance, 2, 6)
                .setDefaultValue(2)
                .setTooltip(new TranslatableText("config.rayon.option.block_distance.tooltip"))
                .setSaveConsumer(newValue -> Config.INSTANCE.blockDistance = newValue)
                .build());

        /* Client Step Rate */
        category.addEntry(builder.entryBuilder().startIntSlider(
                new TranslatableText("config.rayon.option.step_rate"),
                Config.INSTANCE.stepRate, 20, 260)
                .setDefaultValue(60)
                .setTooltip(new TranslatableText("config.rayon.option.step_rate.tooltip"))
                .setTextGetter((currValue) -> currValue == 260 ? new TranslatableText("config.rayon.option.step_rate.max") : new LiteralText(String.valueOf(currValue)))
                .setSaveConsumer(newValue -> Config.INSTANCE.stepRate = newValue)
                .build());

        /* Air Resistance Type */
        category.addEntry(builder.entryBuilder().startEnumSelector(
                new TranslatableText("config.rayon.option.air_resistance_type"), AirHelper.Type.class, Config.INSTANCE.airResistanceType)
                .setDefaultValue(AirHelper.Type.SIMPLE)
                .setTooltip(new TranslatableText("config.rayon.option.air_resistance_type.tooltip"))
                .setEnumNameProvider((value) -> new TranslatableText(((AirHelper.Type) value).getName()))
                .setSaveConsumer(newValue -> Config.INSTANCE.airResistanceType = newValue)
                .build());

        if (!Config.INSTANCE.isRemote) {
            /* Air Density */
            category.addEntry(builder.entryBuilder().startFloatField(
                    new TranslatableText("config.rayon.option.air_density"), Config.INSTANCE.airDensity)
                    .setDefaultValue(1.2f)
                    .setTooltip(new TranslatableText("config.rayon.option.air_density.tooltip"))
                    .setSaveConsumer(newValue -> Config.INSTANCE.airDensity = newValue)
                    .build());

            /* Gravity */
            category.addEntry(builder.entryBuilder().startFloatField(
                    new TranslatableText("config.rayon.option.gravity"), Config.INSTANCE.gravity)
                    .setDefaultValue(-9.81f)
                    .setTooltip(new TranslatableText("config.rayon.option.gravity.tooltip"))
                    .setSaveConsumer(newValue -> Config.INSTANCE.gravity = newValue)
                    .build());
        }

        return builder.setFallbackCategory(category).build();
    }
}
