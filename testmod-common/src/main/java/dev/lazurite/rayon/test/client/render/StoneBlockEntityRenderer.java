package dev.lazurite.rayon.test.client.render;

import com.jme3.math.Quaternion;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.rayon.test.common.entity.StoneBlockEntity;
import dev.lazurite.rayon.test.RayonTest;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class StoneBlockEntityRenderer extends EntityRenderer<StoneBlockEntity> {
    private static final ResourceLocation texture = new ResourceLocation(RayonTest.MODID, "textures/entity/stone_block.png");
    private final StoneBlockEntityModel model;

    public StoneBlockEntityRenderer(EntityRendererProvider.Context ctx, StoneBlockEntityModel model) {
        super(ctx);
        this.model = model;
        this.shadowRadius = 0.2F;
    }

    public void render(StoneBlockEntity cubeEntity, float yaw, float delta, PoseStack matrixStack, MultiBufferSource multiBufferSource, int i) {
        final var rot = Convert.toMinecraft(cubeEntity.getPhysicsRotation(new Quaternion(), delta));
        final var box = cubeEntity.getBoundingBox();

        matrixStack.pushPose();
        matrixStack.mulPose(rot);
        matrixStack.translate(box.getXsize() * -0.5, box.getYsize() * -0.5, box.getZsize() * -0.5);

        final var vertexConsumer = multiBufferSource.getBuffer(model.renderType(this.getTextureLocation(cubeEntity)));
        model.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();

        super.render(cubeEntity, yaw, delta, matrixStack, multiBufferSource, i);
    }

    @Override
    public boolean shouldRender(StoneBlockEntity entity, Frustum frustum, double d, double e, double f) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(StoneBlockEntity entity) {
        return texture;
    }
}