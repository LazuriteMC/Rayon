package dev.lazurite.rayon.mixin.client.render;

import dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

@Mixin(EntityRenderDispatcher.class)
public class DynamicEntityRenderer {
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V",
                    shift = At.Shift.AFTER
            )
    )
    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        DynamicBodyEntity dynamicBody = DynamicBodyEntity.get(entity);

        if (dynamicBody != null) {
            Vector3f pos = VectorHelper.spline(
                    dynamicBody.getCenterOfMassPosition(new Vector3f()),
                    dynamicBody.getTargetPosition(new Vector3f()),
                    dynamicBody.getLinearVelocity(new Vector3f()),
                    dynamicBody.getTargetLinearVelocity(new Vector3f()),
                    dynamicBody.getLinearAcceleration(new Vector3f()),
                    tickDelta);

            Quat4f slerp = QuaternionHelper.slerp(dynamicBody.getOrientation(new Quat4f()), dynamicBody.getTargetOrientation(new Quat4f()), tickDelta);
            matrices.peek().getModel().multiply(QuaternionHelper.quat4fToQuaternion(slerp));
//
//            Vec3d current = VectorHelper.vector3fToVec3d(dynamicBody.getCenterOfMassPosition(new Vector3f()));
//            Vec3d target = VectorHelper.vector3fToVec3d(dynamicBody.getTargetPosition(new Vector3f()));
//            x = MathHelper.lerp(tickDelta, current.x, target.x);
//            y = MathHelper.lerp(tickDelta, current.y, target.y);
//            z = MathHelper.lerp(tickDelta, current.z, target.z);
            x = pos.x;
            y = pos.y;
            z = pos.z;
        }
    }
}
