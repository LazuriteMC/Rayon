package dev.lazurite.rayon.core.common.impl;

import dev.lazurite.rayon.core.common.impl.event.ClientEventHandler;

public class RayonCoreClient {
    public static void init() {
        ClientEventHandler.register();
    }
}
