package dev.lazurite.rayon.impl.mixin.hooks.world;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin handles the joining of world physics threads
 * during the client disconnect phase.
 * @see MinecraftSpace
 */
@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "addEntity", at = @At("HEAD"))
    public void addEntity(int id, Entity entity, CallbackInfo info) {
        if (entity instanceof PhysicsElement) {
            Rayon.THREAD.get(this).execute(
                    space -> space.addCollisionObject(((PhysicsElement) entity).getRigidBody()));
        }
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    public void disconnect(CallbackInfo info) {
        Rayon.THREAD.get((World) (Object) this).execute(MinecraftSpace::destroy);
    }
}
