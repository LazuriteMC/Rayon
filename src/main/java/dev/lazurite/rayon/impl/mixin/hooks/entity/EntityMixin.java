package dev.lazurite.rayon.impl.mixin.hooks.entity;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;

    @Inject(
            method = "toTag",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V"
            )
    )
    public void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
        if (this instanceof PhysicsElement) {
            ((PhysicsElement) this).getRigidBody().toTag(tag);
        }
    }

    @Inject(
            method = "fromTag",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V"
            )
    )
    public void fromTag(CompoundTag tag, CallbackInfo info) {
        if (this instanceof PhysicsElement) {
            ((PhysicsElement) this).getRigidBody().fromTag(tag);
        }
    }

    /**
     * This method cleans up after the {@link MinecraftSpace}
     * by removing any {@link ElementRigidBody}s that have had
     * their entity removed.
     * {@link Entity} removed from the world.
     */
    @Inject(method = "remove", at = @At("HEAD"))
    public synchronized void remove(CallbackInfo info) {
        if (this instanceof PhysicsElement) {
            ElementRigidBody body = ((PhysicsElement) this).getRigidBody();

            if (body.isInWorld()) {
                Rayon.THREAD.get(((Entity) (Object) this).getEntityWorld())
                        .execute(space -> space.removeCollisionObject(body));
            }
        }
    }
}
