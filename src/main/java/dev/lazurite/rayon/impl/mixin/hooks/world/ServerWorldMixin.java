package dev.lazurite.rayon.impl.mixin.hooks.world;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.api.PhysicsElement;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void addEntity(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof PhysicsElement) {
            Rayon.THREAD.get(this).execute(
                    space -> space.addCollisionObject(((PhysicsElement) entity).getRigidBody()));
        }
    }
}
