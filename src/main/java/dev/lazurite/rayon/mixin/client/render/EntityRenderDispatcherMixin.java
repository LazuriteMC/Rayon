package dev.lazurite.rayon.mixin.client.render;

import dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.vecmath.Quat4f;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Unique Quat4f slerp = new Quat4f();
    @Unique Vec3d offset;

//            Vector3f pos = VectorHelper.spline(
//                    dynamicBody.getCenterOfMassPosition(new Vector3f()),
//                    dynamicBody.getTargetPosition(new Vector3f()),
//                    dynamicBody.getLinearVelocity(new Vector3f()),
//                    dynamicBody.getTargetLinearVelocity(new Vector3f()),
//                    dynamicBody.getLinearAcceleration(new Vector3f()),
//                    tickDelta);

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                    shift = At.Shift.BEFORE
            )
    )
    public <E extends Entity> void preRender(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (DynamicBodyEntity.is(entity)) {
            DynamicBodyEntity dynamicBody = DynamicBodyEntity.get(entity);

//            slerp.set(dynamicBody.getOrientation(new Quat4f()));
//            slerp.interpolate(dynamicBody.getOrientation(new Quat4f()), dynamicBody.getTargetOrientation(new Quat4f()), tickDelta);
            slerp.set(QuaternionHelper.slerp(dynamicBody.getOrientation(new Quat4f()), dynamicBody.getTargetOrientation(new Quat4f()), tickDelta));
            matrices.peek().getModel().multiply(QuaternionHelper.quat4fToQuaternion(slerp));

            Box box = entity.getBoundingBox().offset(entity.getPos().negate());
            offset = box.getCenter().add(new Vec3d(-box.getXLength() / 2.0, -box.getYLength(), -box.getZLength() / 2.0));
            matrices.translate(offset.x, offset.y, offset.z);
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                    shift = At.Shift.AFTER
            )
    )
    public <E extends Entity> void postRender(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (DynamicBodyEntity.is(entity)) {
            matrices.translate(-offset.x, -offset.y, -offset.z);

            slerp.conjugate();
            matrices.peek().getModel().multiply(QuaternionHelper.quat4fToQuaternion(slerp));
        }
    }
}
