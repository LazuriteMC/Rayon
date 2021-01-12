package dev.lazurite.rayon.mixin.client.network;

import dev.lazurite.rayon.physics.body.EntityRigidBody;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    /**
     * Cancels the handling of {@link EntityPositionS2CPacket} for {@link EntityRigidBody} objects.
     * @see EntityRigidBody
     */
    @Inject(
            method = "onEntityPosition(Lnet/minecraft/network/packet/s2c/play/EntityPositionS2CPacket;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/world/ClientWorld;getEntityById(I)Lnet/minecraft/entity/Entity;"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void onEntityPosition(EntityPositionS2CPacket packet, CallbackInfo info, Entity entity) {
        if (EntityRigidBody.get(entity) != null) {
            info.cancel();
        }
    }

    /**
     * Cancels the handling of {@link EntityS2CPacket} for {@link EntityRigidBody} objects.
     * @see EntityRigidBody
     */
    @Inject(
            method = "onEntityUpdate(Lnet/minecraft/network/packet/s2c/play/EntityS2CPacket;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/packet/s2c/play/EntityS2CPacket;getEntity(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void onEntityUpdate(EntityS2CPacket packet, CallbackInfo info, Entity entity) {
        if (EntityRigidBody.get(entity) != null) {
            info.cancel();
        }
    }
}
