package dev.lazurite.rayon.core.impl.mixin.client.event;

import dev.lazurite.rayon.core.impl.client.event.BetterClientLifecycleEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow private MinecraftClient client;
    @Shadow private ClientWorld world;

    /**
     * Invokes the game join event.
     * @see BetterClientLifecycleEvents
     */
    @Inject(
            method = "onGameJoin",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        BetterClientLifecycleEvents.GAME_JOIN.invoker().onGameJoin(client, world, client.player);
    }
}
