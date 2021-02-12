package dev.lazurite.rayon.impl.util.config.settings;

import dev.lazurite.rayon.impl.bullet.manager.DebugManager;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;

@Settings
public class LocalSettings {
    @Setting
    @Setting.Constrain.Range(min = 1, max = 5)
    private int blockDistance;

    @Setting
    @Setting.Constrain.Range(min = 3, max = 16, step = 1.0f)
    private int debugDistance;

    @Setting
    private DebugManager.DrawMode debugDrawMode;

    public LocalSettings(int blockDistance, int debugDistance, DebugManager.DrawMode debugDrawMode) {
        this.blockDistance = blockDistance;
        this.debugDistance = debugDistance;
        this.debugDrawMode = debugDrawMode;
    }

    public void setBlockDistance(int blockDistance) {
        this.blockDistance = blockDistance;
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

    public int getDebugDistance() {
        return this.debugDistance;
    }

    public DebugManager.DrawMode getDebugDrawMode() {
        return this.debugDrawMode;
    }
}
