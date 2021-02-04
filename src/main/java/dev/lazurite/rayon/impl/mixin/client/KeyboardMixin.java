package dev.lazurite.rayon.impl.mixin.client;

import dev.lazurite.rayon.impl.mixin.client.render.DebugRendererMixin;
import dev.lazurite.rayon.impl.physics.manager.DebugManager;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.body.BlockRigidBody;
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
 * @see DebugRendererMixin
 */
@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow protected abstract void debugWarn(String string, Object... objects);

    @Inject(method = "processF3", at = @At("HEAD"), cancellable = true)
    private void processF3(int key, CallbackInfoReturnable<Boolean> info) {
        if (key == 82) { // 'r' key
            DebugManager.DebugLayer layer = DebugManager.getInstance().nextLayer();

            if (DebugManager.getInstance().isEnabled()) {
                debugWarn(layer.getTranslation());
            } else {
                debugWarn("debug.rayon.off");
            }

            info.setReturnValue(true);
        }
    }
}
