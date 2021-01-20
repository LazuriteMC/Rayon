package dev.lazurite.rayon.impl.util.config.settings;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;

@Settings
public class LocalSettings {
    @Setting
    @Setting.Constrain.Range(min = 1, max = 5)
    private int blockDistance;

    @Setting
    @Setting.Constrain.Range(min = 20, max = 260, step = 1.0f)
    private int stepRate;

    public LocalSettings(int blockDistance, int stepRate) {
        this.blockDistance = blockDistance;
        this.stepRate = stepRate;
    }

    public void setBlockDistance(int blockDistance) {
        this.blockDistance = blockDistance;
    }

    public void setStepRate(int stepRate) {
        this.stepRate = stepRate;
    }

    public int getBlockDistance() {
        return this.blockDistance;
    }

    public int getStepRate() {
        return this.stepRate;
    }
}
