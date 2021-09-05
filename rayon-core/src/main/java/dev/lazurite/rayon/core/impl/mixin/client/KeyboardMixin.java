package dev.lazurite.rayon.core.impl.mixin.client;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.lazurite.rayon.core.impl.util.debug.CollisionObjectDebugger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds an F3 key combination (F3 + R). It toggles
 * renders for all relevant {@link PhysicsRigidBody} objects.
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
