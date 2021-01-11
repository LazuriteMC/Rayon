package dev.lazurite.rayon.mixin.client.render;

import dev.lazurite.rayon.physics.body.entity.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.util.config.Config;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * This mixin, although it has two methods, is really only mixing into the
 * {@link EntityRenderDispatcher#render} method twice. Once just before the
 * entity is rendered and once just after.
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Unique Quat4f orientation = new Quat4f();
    @Unique Vector3f offset = new Vector3f();

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                    shift = At.Shift.BEFORE
            )
    )
    public <E extends Entity> void preRender(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (EntityRigidBody.is(entity)) {
            EntityRigidBody dynamicBody = EntityRigidBody.get(entity);

//            orientation.set(QuaternionHelper.slerp(dynamicBody.getOrientation(new Quat4f()), dynamicBody.getTargetOrientation(new Quat4f()), tickDelta));
            orientation.set(QuaternionHelper.slerp(dynamicBody.getPrevOrientation(new Quat4f()), dynamicBody.getOrientation(new Quat4f()), tickDelta));
//            orientation.set(QuaternionHelper.slerp(dynamicBody.getOrientation(new Quat4f()), dynamicBody.getPrevOrientation(new Quat4f()), tickDelta));

            matrices.translate(0, entity.getBoundingBox().getYLength() / 2.0, 0);
            matrices.peek().getModel().multiply(QuaternionHelper.quat4fToQuaternion(orientation));
            matrices.translate(0, -entity.getBoundingBox().getYLength() / 2.0, 0);

            Box box = dynamicBody.getBox();
            offset.set(new Vector3f((float) -box.getXLength() / 2.0f, 0, (float) -box.getZLength() / 2.0f));
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
        if (EntityRigidBody.is(entity)) {
            /* Undo the original offset translation */
            matrices.translate(-offset.x, -offset.y, -offset.z);

            /* Undo the original orientation rotation */
            orientation.inverse();
            matrices.translate(0, entity.getBoundingBox().getYLength() / 2.0, 0);
            matrices.peek().getModel().multiply(QuaternionHelper.quat4fToQuaternion(orientation));
            matrices.translate(0, -entity.getBoundingBox().getYLength() / 2.0, 0);
        }
    }
}
