package dev.lazurite.rayon.core.impl.mixin.client;

import dev.lazurite.rayon.core.impl.util.CollisionObjectDebugger;
import dev.lazurite.rayon.core.impl.bullet.collision.body.MinecraftRigidBody;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds an additional F3 key combination (F3 + R). It toggles
 * renders for all {@link MinecraftRigidBody} objects.
 */
@Mixin(Keyboard.class)
@Environment(EnvType.CLIENT)
public abstract class KeyboardMixin {
    @Shadow protected abstract void debugLog(String string, Object... objects);

    @Inject(method = "processF3", at = @At("HEAD"), cancellable = true)
    private void processF3(int key, CallbackInfoReturnable<Boolean> info) {
        if (key == 82) { // 'r' key
            boolean enabled = CollisionObjectDebugger.getInstance().toggle();

            if (enabled) {
                debugLog("debug.rayon.on");
            } else {
                debugLog("debug.rayon.off");
            }

            info.setReturnValue(true);
        }
    }
}
