package io.lazurite.api.mixin;

import io.lazurite.api.client.ClientInitializer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Contains mixins mostly relating to game join operations
 * @author Ethan Johnson
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    /**
     * This mixin sends the client's config file to the server and
     * initializes a new physics world whenever a game is joined.
     * @param packet
     * @param info required by every mixin injection
     */
    @Inject(at = @At("TAIL"), method = "onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        ClientInitializer.remoteLazuriteMods.clear();
    }
}
