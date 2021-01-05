package dev.lazurite.rayon.examplemod.render;

import dev.lazurite.rayon.examplemod.ExampleMod;
import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import dev.lazurite.rayon.examplemod.render.model.RectangularPrismModel;
import dev.lazurite.rayon.physics.rigidbody.entity.DynamicBodyEntity;
import dev.lazurite.rayon.physics.rigidbody.entity.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import javax.vecmath.Quat4f;

@Environment(EnvType.CLIENT)
public class RectangularPrismEntityRenderer extends EntityRenderer<RectangularPrismEntity> {
    public static final Identifier texture = new Identifier(ExampleMod.MODID, "textures/entity/rectangular_prism.png");
    private final RectangularPrismModel model;

    public RectangularPrismEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.shadowRadius = 0.2F;
        this.model = new RectangularPrismModel(16, 16, 16);
    }

    public void render(RectangularPrismEntity rectangularPrism, float yaw, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();

        EntityRigidBody component = DynamicBodyEntity.get(rectangularPrism);

        if (component != null) {
            Quat4f out = new Quat4f();
//            out = QuaternionHelper.slerp(component.getPrevOrientation(), component.getOrientation(), delta);
//            out.interpolate(component.getPrevOrientation(), component.getOrientation(), delta);
            out.set(component.getOrientation(new Quat4f()));
            matrixStack.peek().getModel().multiply(QuaternionHelper.quat4fToQuaternion(out));
        }

        matrixStack.translate(-0.25, -0.25, -0.25);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(this.getTexture(rectangularPrism)));
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();

        super.render(rectangularPrism, yaw, delta, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public boolean shouldRender(RectangularPrismEntity rectangularPrism, Frustum frustum, double x, double y, double z) {
        return rectangularPrism.shouldRender(x, y, z);
    }

    @Override
    public Identifier getTexture(RectangularPrismEntity rectangularPrism) {
        return texture;
    }
}
