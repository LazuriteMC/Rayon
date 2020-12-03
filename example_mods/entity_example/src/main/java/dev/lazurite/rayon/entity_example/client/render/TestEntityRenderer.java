package dev.lazurite.rayon.entity_example.client.render;

import dev.lazurite.rayon.entity_example.server.ServerInitializer;
import dev.lazurite.rayon.entity_example.server.entity.TestEntity;
import dev.lazurite.rayon.physics.handler.ClientPhysicsHandler;
import dev.lazurite.rayon.helper.QuaternionHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class TestEntityRenderer extends EntityRenderer<TestEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/pig/pig.png");

    private final PigEntityModel<TestEntity> model;

    protected TestEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        model = new PigEntityModel<>();
    }

    @Override
    public void render(TestEntity testEntity, float yaw, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        ClientPhysicsHandler physics = ((ClientPhysicsHandler) testEntity.getPhysics());
        matrixStack.push();

        /* Rotate the entity according to the Rigid Body */
        Quaternion orientation = QuaternionHelper.quat4fToQuaternion(physics.getOrientation(delta));
        matrixStack.peek().getModel().multiply(orientation);

        /* Render the entity */
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(getTexture(testEntity)));
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.pop();
        super.render(testEntity, yaw, delta, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(TestEntity entity) {
        return TEXTURE;
    }

    public static void register() {
        EntityRendererRegistry.INSTANCE.register(ServerInitializer.TEST_ENTITY, (entityRenderDispatcher, context) -> new TestEntityRenderer(entityRenderDispatcher));
    }
}
