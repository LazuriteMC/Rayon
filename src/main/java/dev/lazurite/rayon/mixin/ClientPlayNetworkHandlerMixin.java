package dev.lazurite.rayon.mixin;

import dev.lazurite.rayon.side.client.PhysicsWorld;
import dev.lazurite.rayon.entity.PhysicsEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Contains mixins mostly relating to game join operations
 * @author Ethan Johnson
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    /**
     * @param packet the game join packet
     * @param info required by every mixin injection
     */
    @Inject(at = @At("TAIL"), method = "onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        PhysicsWorld.create();
    }

    /**
     * This mixin cancels all {@link Entity} position updates from the server if it receives
     * information for a {@link PhysicsEntity}.
     * @param packet
     * @param info required by every mixin injection
     * @param entity the {@link Entity} on which the injection point was originally called
     */
    @Inject(
            method = "onEntityPosition(Lnet/minecraft/network/packet/s2c/play/EntityPositionS2CPacket;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/world/ClientWorld;getEntityById(I)Lnet/minecraft/entity/Entity;"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void onEntityPosition(EntityPositionS2CPacket packet, CallbackInfo info, Entity entity) {
        if (entity instanceof PhysicsEntity) {
            info.cancel();
        }
    }

    /**
     * This mixin cancels all {@link Entity} movement updates from the server if it receives
     * information for a {@link PhysicsEntity}.
     * @param packet
     * @param info required by every mixin injection
     * @param entity the {@link Entity} on which the injection point was originally called
     */
    @Inject(
            method = "onEntityUpdate(Lnet/minecraft/network/packet/s2c/play/EntityS2CPacket;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/packet/s2c/play/EntityS2CPacket;getEntity(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void onEntityUpdate(EntityS2CPacket packet, CallbackInfo info, Entity entity) {
        if (entity instanceof PhysicsEntity) {
            info.cancel();
        }
    }
}
