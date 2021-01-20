package dev.lazurite.rayon.impl.util.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuEntry implements ModMenuApi {
    /**
     * Adds the config screen mod menu.
     * @return the {@link ConfigScreenFactory}
     * @see ConfigScreen
     */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::create;
    }
}
