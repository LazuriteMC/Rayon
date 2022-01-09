package dev.lazurite.rayon.impl.fabric;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.fabric.handler.EntityNetworkingClientHandler;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.fabric.handler.EntityNetworkingServerHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

public class RayonFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        Rayon.init();
        EntityNetworkingServerHandler.register();
    }

    @Override
    public void onInitializeClient() {
        Rayon.initClient();
        EntityNetworkingClientHandler.register();
    }
}
