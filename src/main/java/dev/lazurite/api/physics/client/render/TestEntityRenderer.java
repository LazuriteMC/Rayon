package dev.lazurite.api.physics.client.render;

import dev.lazurite.api.physics.client.handler.ClientPhysicsHandler;
import dev.lazurite.api.physics.server.entity.TestEntity;
import dev.lazurite.api.physics.util.math.QuaternionHelper;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class TestEntityRenderer extends EntityRenderer<TestEntity> {

    public TestEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    public void render(TestEntity testEntity, float yaw, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.shadowRadius = 0.2F;

        matrixStack.push();
        ClientPhysicsHandler physics = (ClientPhysicsHandler) testEntity.getPhysics();
        if (physics.isActive()) {
            matrixStack.peek().getModel().multiply(QuaternionHelper.quat4fToQuaternion(physics.getOrientation()));
        }

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(this.getTexture(testEntity)));
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();

        super.render(testEntity, yaw, delta, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(TestEntity entity) {
        return null;
    }
}
