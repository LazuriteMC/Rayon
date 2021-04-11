package dev.lazurite.rayon.entity.testmod.client.render;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.entity.testmod.client.render.model.CubeEntityModel;
import dev.lazurite.rayon.entity.testmod.common.entity.CubeEntity;
import dev.lazurite.rayon.entity.testmod.EntityTestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class CubeEntityRenderer extends EntityRenderer<CubeEntity> {
    public static final Identifier texture = new Identifier(EntityTestMod.MODID, "textures/entity/cube.png");
    public final CubeEntityModel model;

    public CubeEntityRenderer(EntityRenderDispatcher dispatcher, CubeEntityModel model) {
        super(dispatcher);
        this.model = model;
        this.shadowRadius = 0.2F;
    }

    public void render(CubeEntity cubeEntity, float yaw, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        BoundingBox box = cubeEntity.getRigidBody().getFrame().getBox(new BoundingBox(), delta);
        Vector3f bounds = box.getExtent(new Vector3f()).multLocal(-1);
        Quaternion rot = QuaternionHelper.bulletToMinecraft(cubeEntity.getPhysicsRotation(new com.jme3.math.Quaternion(), delta));

        matrixStack.push();
        matrixStack.multiply(rot);
        matrixStack.translate(bounds.x, bounds.y, bounds.z);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(this.getTexture(cubeEntity)));
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();

        super.render(cubeEntity, yaw, delta, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(CubeEntity cubeEntity) {
        return texture;
    }
}
