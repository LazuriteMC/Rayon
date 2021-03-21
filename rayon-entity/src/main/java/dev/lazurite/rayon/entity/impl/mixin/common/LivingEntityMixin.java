package dev.lazurite.rayon.entity.impl.mixin.common;

import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.util.ElementSpawnS2C;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Replaces {@link MobSpawnS2CPacket}.
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "createSpawnPacket", at = @At("HEAD"), cancellable = true)
    public void createSpawnPacket(CallbackInfoReturnable<Packet<?>> info) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof EntityPhysicsElement) {
            info.setReturnValue(ElementSpawnS2C.create((EntityPhysicsElement) entity));
        }
    }
}
