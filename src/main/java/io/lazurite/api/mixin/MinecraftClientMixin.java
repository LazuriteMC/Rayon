package io.lazurite.api.mixin;

import io.lazurite.api.client.CameraUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    /**
     * If the {@link CameraUtil} class locks the camera, don't let anything change it.
     * @param entity the entity to set the camera to
     * @param info required by every mixin injection
     */
    @Inject(at = @At("HEAD"), method = "setCameraEntity", cancellable = true)
    public void setCameraEntity(Entity entity, CallbackInfo info) {
        if (CameraUtil.isLocked()) {
            info.cancel();
        }
    }
}
