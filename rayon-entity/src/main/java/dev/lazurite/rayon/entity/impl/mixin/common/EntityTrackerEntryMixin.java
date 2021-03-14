package dev.lazurite.rayon.entity.impl.mixin.common;

import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.entity.impl.net.ElementMovementS2C;
import dev.lazurite.rayon.entity.impl.net.ElementPropertiesS2C;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {
    @Shadow @Final private Entity entity;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/EntityTrackerEntry;syncEntityData()V"
            )
    )
    public void sendMovementUpdates(CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();

            if (rigidBody.isActive() && rigidBody.getPriorityPlayer() == null) {
                ElementMovementS2C.send((EntityPhysicsElement) entity);
            }

            if (rigidBody.arePropertiesDirty()) {
                ElementPropertiesS2C.send((EntityPhysicsElement) entity);
                rigidBody.setPropertiesDirty(false);
            }
        }
    }
}
