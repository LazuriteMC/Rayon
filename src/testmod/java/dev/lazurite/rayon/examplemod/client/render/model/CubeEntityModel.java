package dev.lazurite.rayon.examplemod.client.render.model;

import dev.lazurite.rayon.examplemod.common.entity.CubeEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class CubeEntityModel extends EntityModel<CubeEntity> {
    private final ModelPart base;

    public CubeEntityModel() {
        base = new ModelPart(this, 0, 0);
        base.addCuboid(0, 0, 0, 8, 8, 8);
    }

    @Override
    public void setAngles(CubeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) { }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        base.render(matrices, vertexConsumer, light, overlay);
    }
}
