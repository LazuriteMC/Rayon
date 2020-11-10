package dev.lazurite.api.mixin;

import dev.lazurite.api.LazuriteAPI;
import dev.lazurite.api.network.packet.ModdedServerS2C;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * This mixin class modifies the way sound works in minecraft. Normally, the distance to a sound is
 * calculated using the player's position and the source of the sound's position. When moving the camera, this
 * system doesn't work so well. Instead, the distance is now calculated between the source and the camera rather
 * than the player's actual position.
 * @author Ethan Johnson
 */
@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow @Final List<ServerPlayerEntity> players;

    /**
     * This method allows the server to tell the client that it is modded upon player connect.
     * @param connection
     * @param player
     * @param info required by every mixin injection
     */
    @Inject(at = @At("TAIL"), method = "onPlayerConnect")
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        ModdedServerS2C.send(player, LazuriteAPI.MODID);
    }
}
