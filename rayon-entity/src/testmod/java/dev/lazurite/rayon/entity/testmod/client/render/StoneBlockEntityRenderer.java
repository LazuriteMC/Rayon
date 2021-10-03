package dev.lazurite.rayon.entity.testmod.client.render;

import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.entity.testmod.common.entity.StoneBlockEntity;
import dev.lazurite.rayon.entity.testmod.EntityTestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class StoneBlockEntityRenderer extends EntityRenderer<StoneBlockEntity> {
    private static final Identifier texture = new Identifier(EntityTestMod.MODID, "textures/entity/stone_block.png");
    private final StoneBlockEntityModel model;

    public StoneBlockEntityRenderer(EntityRendererFactory.Context ctx, StoneBlockEntityModel model) {
        super(ctx);
        this.model = model;
        this.shadowRadius = 0.2F;
    }

    public void render(StoneBlockEntity cubeEntity, float yaw, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        var box = Convert.toBullet(cubeEntity.getBoundingBox());
        var rot = Convert.toMinecraft(cubeEntity.getPhysicsRotation(new com.jme3.math.Quaternion(), delta));

        matrixStack.push();
        matrixStack.multiply(rot);
        matrixStack.translate(box.getXExtent() * -0.5, box.getYExtent() * -0.5, box.getZExtent() * -0.5);
        var vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(this.getTexture(cubeEntity)));
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();

        super.render(cubeEntity, yaw, delta, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(StoneBlockEntity cubeEntity) {
        return texture;
    }
}
