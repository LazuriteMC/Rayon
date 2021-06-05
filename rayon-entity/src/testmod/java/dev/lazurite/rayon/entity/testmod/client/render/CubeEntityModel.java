package dev.lazurite.rayon.entity.testmod.client.render;

import dev.lazurite.rayon.entity.testmod.common.entity.CubeEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.HashMap;

public class CubeEntityModel extends EntityModel<CubeEntity> {
    private final ModelPart modelPart;

    public CubeEntityModel(int size) {
        var cuboidData = ModelPartBuilder.create().cuboid(0, 0, 0, size, size, size).build();
        var cuboids = new ArrayList<ModelPart.Cuboid>();

        for (var data : cuboidData) {
            cuboids.add(data.createCuboid(32, 32));
        }

       modelPart = new ModelPart(cuboids, new HashMap<>());
    }

    @Override
    public void setAngles(CubeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
