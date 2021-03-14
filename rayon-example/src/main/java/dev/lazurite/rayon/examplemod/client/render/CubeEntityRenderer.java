package dev.lazurite.rayon.examplemod.client.render;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.examplemod.ExampleMod;
import dev.lazurite.rayon.examplemod.common.entity.CubeEntity;
import dev.lazurite.rayon.examplemod.client.render.model.CubeEntityModel;
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
    public static final Identifier texture = new Identifier(ExampleMod.MODID, "textures/entity/cube.png");
    private final CubeEntityModel model = new CubeEntityModel();

    public CubeEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.shadowRadius = 0.2F;
    }

    public void render(CubeEntity cubeEntity, float yaw, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Vector3f bounds = cubeEntity.getRigidBody().getCollisionShape().boundingBox(new Vector3f(), new com.jme3.math.Quaternion(), new BoundingBox()).getExtent(new Vector3f()).multLocal(-1);
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
