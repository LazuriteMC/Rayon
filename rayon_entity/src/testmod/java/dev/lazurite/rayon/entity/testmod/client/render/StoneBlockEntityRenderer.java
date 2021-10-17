package dev.lazurite.rayon.entity.testmod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.entity.testmod.EntityTestMod;
import dev.lazurite.rayon.entity.testmod.common.entity.StoneBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class StoneBlockEntityRenderer extends EntityRenderer<StoneBlockEntity> {
    private static final ResourceLocation texture = new ResourceLocation(EntityTestMod.MODID, "textures/entity/stone_block.png");
    private final StoneBlockEntityModel model;

    public StoneBlockEntityRenderer(EntityRendererProvider.Context ctx, StoneBlockEntityModel model) {
        super(ctx);
        this.model = model;
        this.shadowRadius = 0.2F;
    }

    public void render(StoneBlockEntity cubeEntity, float yaw, float delta, PoseStack matrixStack, MultiBufferSource multiBufferSource, int i) {
        var box = Convert.toBullet(cubeEntity.getBoundingBox());
        var rot = Convert.toMinecraft(cubeEntity.getPhysicsRotation(new com.jme3.math.Quaternion(), delta));

        matrixStack.pushPose();
        matrixStack.mulPose(rot);
        matrixStack.translate(box.getXExtent() * -0.5, box.getYExtent() * -0.5, box.getZExtent() * -0.5);
        var vertexConsumer = multiBufferSource.getBuffer(model.renderType(this.getTextureLocation(cubeEntity)));
        model.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();

        super.render(cubeEntity, yaw, delta, matrixStack, multiBufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(StoneBlockEntity entity) {
        return texture;
    }
}
