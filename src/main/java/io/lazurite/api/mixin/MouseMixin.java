package io.lazurite.api.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin class disables most functionality of the mouse
 * in-game when the player is manipulating the camera.
 * @author Ethan Johnson
 */
@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    /**
     * This mixin redirects the {@link ClientPlayerEntity#changeLookDirection(double, double)} method
     * so that when the mouse is moved while the camera is being manipulated, nothing happens.
     * @param player the client player
     * @param cursorDeltaX the x cursor position
     * @param cursorDeltaY the y cursor position
     */
    @Redirect(
            method = "updateMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
            )
    )
    public void changeLookDirection(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY) {
        if (client.getCameraEntity() == client.player) {
            player.changeLookDirection(cursorDeltaX, cursorDeltaY);
        }
    }

    /**
     * This mixin redirects the {@link KeyBinding#setKeyPressed(InputUtil.Key, boolean)} method
     * so that when the player is manipulating the camera, it is not called.
     * @param key the key being pressed
     * @param pressed whether or not the key is pressed
     */
    @Redirect(
            method = "onMouseButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/options/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V"
            )
    )
    public void setKeyPressed(InputUtil.Key key, boolean pressed) {
        KeyBinding.setKeyPressed(key, client.getCameraEntity() == client.player && pressed);
    }

    /**
     * This mixin redirects the {@link KeyBinding#onKeyPressed(InputUtil.Key)} method
     * so that when the player is manipulating the camera, it is not called.
     * @param key the key being pressed
     */
    @Redirect(
            method = "onMouseButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/options/KeyBinding;onKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;)V"
            )
    )
    public void onKeyPressed(InputUtil.Key key) {
        if (client.getCameraEntity() == client.player) {
            KeyBinding.onKeyPressed(key);
        }
    }
}