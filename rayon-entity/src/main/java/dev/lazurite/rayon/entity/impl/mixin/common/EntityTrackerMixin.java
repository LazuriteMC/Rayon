package dev.lazurite.rayon.entity.impl.mixin.common;

import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/world/ThreadedAnvilChunkStorage$EntityTracker")
public class EntityTrackerMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "sendToOtherNearbyPlayers", at = @At("HEAD"), cancellable = true)
    public void sendToOtherNearbyPlayers(Packet<?> packet, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            if (packet instanceof EntityS2CPacket || packet instanceof EntityPositionS2CPacket || packet instanceof EntityVelocityUpdateS2CPacket) {
                info.cancel();
            }
        }
    }
}
