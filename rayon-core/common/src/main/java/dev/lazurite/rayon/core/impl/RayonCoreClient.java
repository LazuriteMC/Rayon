package dev.lazurite.rayon.core.impl;

import dev.lazurite.rayon.core.impl.event.ClientEventHandler;

public class RayonCoreClient {
    public static void init() {
        ClientEventHandler.register();
    }
}
