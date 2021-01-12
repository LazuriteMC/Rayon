package dev.lazurite.rayon.mixin.client;

import dev.lazurite.rayon.util.config.Config;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.physics.body.BlockRigidBody;
import dev.lazurite.rayon.physics.helper.DebugHelper;
import dev.lazurite.rayon.mixin.client.render.DebugRendererMixin;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds an additional F3 key combination (F3 + R). When pressed
 * once, it enables rendering of {@link EntityRigidBody} objects.
 * When it's pressed again, it also enabled rendering of
 * {@link BlockRigidBody} objects in a different color.
 * @see DebugHelper
 * @see DebugRendererMixin
 */
@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow protected abstract void debugWarn(String string, Object... objects);

    @Inject(method = "processF3", at = @At("HEAD"), cancellable = true)
    private void processF3(int key, CallbackInfoReturnable<Boolean> info) {
        if (key == 82) { // 'r' key
            if (Config.INSTANCE.debug && Config.INSTANCE.debugBlocks) {
                Config.INSTANCE.debug = false;
                Config.INSTANCE.debugBlocks = false;
                debugWarn("debug.rayon.off");
            } else if (Config.INSTANCE.debug) {
                Config.INSTANCE.debugBlocks = true;
            } else {
                Config.INSTANCE.debug = true;
                debugWarn("debug.rayon.on");
            }

            info.setReturnValue(true);
        }
    }
}
