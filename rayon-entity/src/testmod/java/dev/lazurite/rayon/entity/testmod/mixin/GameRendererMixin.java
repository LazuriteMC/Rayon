package dev.lazurite.rayon.entity.testmod.mixin;

import com.jme3.math.Quaternion;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.testmod.common.entity.CubeEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin is mainly for manipulating the player's camera.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow @Final private Camera camera;

	@Inject(method = "renderWorld", at = @At("HEAD"))
	public void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
		if (camera.getFocusedEntity() instanceof CubeEntity) {
			CubeEntity quadcopter = (CubeEntity) camera.getFocusedEntity();

			Quaternion q = quadcopter.getPhysicsRotation(new Quaternion(), tickDelta);
			q.set(q.getX(), -q.getY(), q.getZ(), -q.getW());

			Matrix4f newMat = new Matrix4f(QuaternionHelper.bulletToMinecraft(q));
			newMat.transpose();
			matrix.peek().getModel().multiply(newMat);
		}
	}

	@Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
	private void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo info) {
		if (camera.getFocusedEntity() instanceof EntityPhysicsElement) {
			info.cancel();
		}
	}

	@ModifyArg(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V", ordinal = 2))
	public net.minecraft.util.math.Quaternion multiplyYaw(net.minecraft.util.math.Quaternion quaternion) {
		if (camera.getFocusedEntity() instanceof EntityPhysicsElement) {
			return QuaternionHelper.bulletToMinecraft(QuaternionHelper.rotateY(new Quaternion(), 180));
		}

		return quaternion;
	}

	@ModifyArg(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V", ordinal = 3))
	public net.minecraft.util.math.Quaternion multiplyPitch(net.minecraft.util.math.Quaternion quaternion) {
		if (camera.getFocusedEntity() instanceof EntityPhysicsElement) {
			return QuaternionHelper.bulletToMinecraft(new Quaternion());
		}

		return quaternion;
	}
}