package dev.lazurite.rayon.impl.util.config.settings;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import dev.lazurite.rayon.impl.util.config.ConfigS2C;

/**
 * This class contains settings that <b>must</b> be the
 * same between the client and the server in order for
 * the physics simulation to function properly.
 * @see ConfigS2C
 */
@Settings
public class GlobalSettings {
    @Setting
    private float gravity;

    @Setting
    @Setting.Constrain.Range(min = 0.0f)
    private float airDensity;

    @Setting
    private boolean airResistanceEnabled;

    public GlobalSettings(float gravity, float airDensity, boolean doAirResistance) {
        this.gravity = gravity;
        this.airDensity = airDensity;
        this.airResistanceEnabled = doAirResistance;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setAirDensity(float airDensity) {
        this.airDensity = airDensity;
    }

    public void setAirResistanceEnabled(boolean doAirResistance) {
        this.airResistanceEnabled = doAirResistance;
    }

    public float getGravity() {
        return this.gravity;
    }

    public float getAirDensity() {
        return this.airDensity;
    }

    public boolean isAirResistanceEnabled() {
        return this.airResistanceEnabled;
    }
}
