package dev.lazurite.rayon.render;

import dev.lazurite.rayon.physics.DynamicBody;
import dev.lazurite.rayon.physics.composition.DynamicBodyComposition;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import javax.vecmath.Quat4f;

public abstract class DynamicBodyRenderer<T extends Entity, M extends EntityModel<T>> extends EntityRenderer<T> {
    protected M model;

    public DynamicBodyRenderer(EntityRenderDispatcher dispatcher, M model, float shadowRadius) {
        super(dispatcher);
        this.model = model;
        this.shadowRadius = shadowRadius;
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light) {
        matrixStack.push();

        DynamicBodyComposition physics = ((DynamicBody) entity).getDynamicBody();
        Quat4f orientation = physics.getSynchronizer().get(DynamicBodyComposition.ORIENTATION);
        matrixStack.peek().getModel().multiply(QuaternionHelper.quat4fToQuaternion(orientation));

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(getTexture(entity)));
        model.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.pop();
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);
    }

    public M getModel() {
        return this.model;
    }
}
