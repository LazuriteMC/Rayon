package io.lazurite.api.mixin;

import io.lazurite.api.client.ClientInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin class modifies the behavior of the entity renderer
 * such that the client player will render even in first-person view.
 * The reason for this is then the player can be seen while manipulating the camera.
 * @author Ethan Johnson
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final MinecraftClient client;

    /**
     * This mixin modifies the {@link Camera#getFocusedEntity()} method
     * to return the {@link net.minecraft.client.network.ClientPlayerEntity}
     * whenever {@link ClientInitializer#shouldRenderPlayer} is true.
     * @param camera the camera object
     * @return the focused entity
     */
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;",
                    ordinal = 3
            )
    )
    public Entity getFocusedEntity(Camera camera) {
        if (ClientInitializer.shouldRenderPlayer) {
            return client.player;
        }

        return camera.getFocusedEntity();
    }
}
