package dev.lazurite.rayon.impl.bullet.collision.space.supplier.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public class ClientEntitySupplier implements EntitySupplier {
    @Override
    public GameType getGameType(Player player) {
        var client = Minecraft.getInstance();
        var id = player.getUUID();

        // Is client player
        if (client.player != null && client.player.getUUID().equals(id) && client.gameMode != null) {
            return client.gameMode.getPlayerMode();
        }

        // Is remote player
        var connection = Minecraft.getInstance().getConnection();
        if (connection != null && connection.getOnlinePlayerIds().contains(id)) {
            var playerInfo = connection.getPlayerInfo(id);
            return playerInfo == null ? GameType.SURVIVAL : playerInfo.getGameMode();
        }

        return GameType.SURVIVAL;
    }
}
