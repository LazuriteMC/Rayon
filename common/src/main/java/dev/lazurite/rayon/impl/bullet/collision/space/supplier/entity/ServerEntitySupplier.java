package dev.lazurite.rayon.impl.bullet.collision.space.supplier.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public class ServerEntitySupplier implements EntitySupplier {
    @Override
    public GameType getGameType(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return serverPlayer.level().getServer().createGameModeForPlayer(serverPlayer).getGameModeForPlayer();
        }
        return GameType.SURVIVAL;
    }
}
