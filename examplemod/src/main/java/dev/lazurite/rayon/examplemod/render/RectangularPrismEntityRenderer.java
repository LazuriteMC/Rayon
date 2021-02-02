package dev.lazurite.rayon.examplemod.render;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.examplemod.ExampleMod;
import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import dev.lazurite.rayon.examplemod.render.model.RectangularPrismModel;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.util.helper.math.QuaternionHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

@Environment(EnvType.CLIENT)
public class RectangularPrismEntityRenderer extends EntityRenderer<RectangularPrismEntity> {
    public static final Identifier texture = new Identifier(ExampleMod.MODID, "textures/entity/rectangular_prism.png");
    private final RectangularPrismModel model;

    public RectangularPrismEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.shadowRadius = 0.2F;
        this.model = new RectangularPrismModel(16, 32, 16);
    }

    public void render(RectangularPrismEntity rectangularPrism, float yaw, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();

        EntityRigidBody body = Rayon.ENTITY.get(rectangularPrism);
        Box box = rectangularPrism.getBoundingBox();

        matrixStack.translate(0, body.boundingBox(new BoundingBox()).getYExtent() / 2.0, 0);
        matrixStack.multiply(QuaternionHelper.bulletToMinecraft(body.getPhysicsRotation(new Quaternion(), delta)));
        matrixStack.translate(-box.getXLength() / 2.0, -box.getYLength() / 2.0, -box.getZLength() / 2.0);

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(this.getTexture(rectangularPrism)));
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
//        MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(Items.DIAMOND), ModelTransformation.Mode.GROUND, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
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
