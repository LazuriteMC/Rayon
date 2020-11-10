package dev.lazurite.api.mixin;

import dev.lazurite.api.client.LazuriteClient;
import dev.lazurite.api.client.physics.PhysicsWorld;
import dev.lazurite.api.network.packet.ConfigC2S;
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
     * This mixin calls the onGameJoin method from LazuriteClient.
     * @param packet the game join packet
     * @param info required by every mixin injection
     */
    @Inject(at = @At("TAIL"), method = "onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
//        LazuriteClient.remoteLazuriteMods.clear();
        LazuriteClient.physicsWorld = new PhysicsWorld();
        ConfigC2S.send(LazuriteClient.config);
    }
}
