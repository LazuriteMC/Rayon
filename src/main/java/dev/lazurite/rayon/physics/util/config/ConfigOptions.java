package dev.lazurite.rayon.physics.util.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class ConfigOptions {
    public static final DoubleOption GRAVITY = new DoubleOption(
        "config.rayon.option.gravity", -20, 0, 0.01F,
        (gameOptions) -> (double) Config.INSTANCE.gravity,
        (gameOptions, gravity) -> Config.INSTANCE.gravity = gravity.floatValue(),
        (gameOptions, option) -> {
            option.setTooltip(MinecraftClient.getInstance().textRenderer.wrapLines(new TranslatableText("config.rayon.option.gravity.tooltip"), 200));
            return option.getGenericLabel(new LiteralText(String.valueOf(Config.INSTANCE.gravity)));
        });

    public static final DoubleOption BLOCK_DISTANCE = new DoubleOption(
        "config.rayon.option.block_distance", 2, 6, 1.0F,
        (gameOptions) -> (double) Config.INSTANCE.blockDistance,
        (gameOptions, blockDistance) -> Config.INSTANCE.blockDistance = blockDistance.intValue(),
        (gameOptions, option) -> {
            option.setTooltip(MinecraftClient.getInstance().textRenderer.wrapLines(new TranslatableText("config.rayon.option.block_distance.tooltip"), 200));
            return option.getGenericLabel(new LiteralText(String.valueOf(Config.INSTANCE.blockDistance)));
        });

    public static final DoubleOption ENTITY_DISTANCE = new DoubleOption(
            "config.rayon.option.entity_distance", 5, 25, 1.0F,
            (gameOptions) -> (double) Config.INSTANCE.entityDistance,
            (gameOptions, entityDistance) -> Config.INSTANCE.entityDistance = entityDistance.intValue(),
            (gameOptions, option) -> {
                option.setTooltip(MinecraftClient.getInstance().textRenderer.wrapLines(new TranslatableText("config.rayon.option.entity_distance.tooltip"), 200));
                return option.getGenericLabel(new LiteralText(String.valueOf(Config.INSTANCE.entityDistance)));
            });

    public static final DoubleOption STEP_RATE = new DoubleOption(
        "config.rayon.option.step_rate", 20, 260, 10,
        (gameOptions) -> (double) Config.INSTANCE.stepRate,
        (gameOptions, stepRate) -> Config.INSTANCE.stepRate = stepRate.intValue(),
        (gameOptions, option) -> {
            option.setTooltip(MinecraftClient.getInstance().textRenderer.wrapLines(new TranslatableText("config.rayon.option.step_rate.tooltip"), 200));
            if (Config.INSTANCE.stepRate < 260) {
                return option.getGenericLabel(new LiteralText(String.valueOf(Config.INSTANCE.stepRate)));
            } else {
                return option.getGenericLabel(new TranslatableText("config.rayon.option.step_rate.max"));
            }
        });
}
