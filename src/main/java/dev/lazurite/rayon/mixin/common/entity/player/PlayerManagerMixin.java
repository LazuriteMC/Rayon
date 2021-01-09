package dev.lazurite.rayon.mixin.common.entity.player;

import dev.lazurite.rayon.util.config.Config;
import dev.lazurite.rayon.util.config.ConfigS2C;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * This mixin adds a call to {@link PlayerManager#onPlayerConnect}
 * which sends a packet containing the server's {@link Config} for
 * the client to use after it has joined the game. This ultimately
 * gives the server control over important config values which
 * heavily affect the simulation such as air density and gravity.
 * @see Config
 * @see ConfigS2C
 */
@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;onSpawn()V",
                    shift = At.Shift.BEFORE
            )
    )
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        ConfigS2C.send(player, Config.INSTANCE);
    }
}
