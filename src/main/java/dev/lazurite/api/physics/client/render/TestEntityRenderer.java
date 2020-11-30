package dev.lazurite.api.physics.example.client.render;

import dev.lazurite.api.physics.client.handler.ClientPhysicsHandler;
import dev.lazurite.api.physics.server.ServerInitializer;
import dev.lazurite.api.physics.server.entity.TestEntity;
import dev.lazurite.api.physics.client.helper.QuaternionHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class TestEntityRenderer extends EntityRenderer<TestEntity> {
    private final EntityModel model = new BeeEntityModel();

    public TestEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    public static void register() {
        EntityRendererRegistry.INSTANCE.register(ServerInitializer.TEST_ENTITY, (entityRenderDispatcher, context)->new TestEntityRenderer(entityRenderDispatcher));
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
