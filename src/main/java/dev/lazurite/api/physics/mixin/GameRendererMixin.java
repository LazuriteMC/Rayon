package dev.lazurite.api.physics.mixin;

import dev.lazurite.api.physics.client.ClientInitializer;
import dev.lazurite.api.physics.client.PhysicsWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Ethan Johnson
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow @Final private MinecraftClient client;

	/**
	 * @param tickDelta minecraft tick delta
	 * @param limitTime
	 * @param matrix
	 * @param info required by every mixin injection
	 */
	@Inject(at = @At("HEAD"), method = "renderWorld")
	public void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
		PhysicsWorld w = PhysicsWorld.getInstance();

		if (w != null) {
			if (!client.isPaused())
				w.stepWorld();
			else w.clock.reset();
		}
	}
}
