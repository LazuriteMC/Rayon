package dev.lazurite.rayon.impl.util.config.settings;

import dev.lazurite.rayon.impl.util.DebugManager;
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

    @Setting
    @Setting.Constrain.Range(min = 5, max = 10, step = 1.0f)
    private int maxSubSteps;

    @Setting
    @Setting.Constrain.Range(min = 3, max = 32, step = 1.0f)
    private int debugDistance;

    @Setting
    private DebugManager.DrawMode debugDrawMode;

    public LocalSettings(int blockDistance, int stepRate, int maxSubSteps, int debugDistance, DebugManager.DrawMode debugDrawMode) {
        this.blockDistance = blockDistance;
        this.stepRate = stepRate;
        this.maxSubSteps = maxSubSteps;
        this.debugDistance = debugDistance;
        this.debugDrawMode = debugDrawMode;
    }

    public void setBlockDistance(int blockDistance) {
        this.blockDistance = blockDistance;
    }

    public void setStepRate(int stepRate) {
        this.stepRate = stepRate;
    }

    public void setMaxSubSteps(int maxSubSteps) {
        this.maxSubSteps = maxSubSteps;
    }

    public void setDebugDistance(int debugDistance) {
        this.debugDistance = debugDistance;
    }

    public void setDebugDrawMode(DebugManager.DrawMode debugDrawMode) {
        this.debugDrawMode = debugDrawMode;
    }

    public int getBlockDistance() {
        return this.blockDistance;
    }

    public int getStepRate() {
        return this.stepRate;
    }

    public int getMaxSubSteps() {
        return this.maxSubSteps;
    }

    public int getDebugDistance() {
        return this.debugDistance;
    }

    public DebugManager.DrawMode getDebugDrawMode() {
        return this.debugDrawMode;
    }
}