package dev.lazurite.rayon.examplemod.render.model;

import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3i;

@Environment(EnvType.CLIENT)
public class RectangularPrismModel extends EntityModel<RectangularPrismEntity> {
    private final ModelPart base;
    private final int x;
    private final int y;
    private final int z;

    public RectangularPrismModel(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        base = new ModelPart(this, 0, 0);
        base.addCuboid(0, 0, 0, x / 2.0f, y / 2.0f, z / 2.0f);
    }

    @Override
    public void setAngles(RectangularPrismEntity entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        base.render(matrices, vertexConsumer, light, overlay);
    }

    public Vec3i getBounds() {
        return new Vec3i(x, y, z);
    }
}
