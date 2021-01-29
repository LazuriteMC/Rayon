package dev.lazurite.rayon.impl.mixin.client;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.DebugShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.physics.body.type.DebuggableBody;
import dev.lazurite.rayon.impl.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo info) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        World world = MinecraftClient.getInstance().world;

        if (Config.getInstance().debug) {
            for (PhysicsRigidBody body : Rayon.WORLD.get(world).getRigidBodyList()) {
                if (body instanceof DebuggableBody) {
                    if (((DebuggableBody) body).getDebugLayer() <= Config.getInstance().debugLayer) {

                        RenderSystem.pushMatrix();
                        RenderSystem.disableTexture();
                        RenderSystem.depthMask(false);
                        RenderSystem.lineWidth(1.0F);

                        FloatBuffer buffer = DebugShapeFactory.getDebugTriangles(body.getCollisionShape(), DebugShapeFactory.lowResolution).rewind();
                        BufferBuilder builder = Tessellator.getInstance().getBuffer();
                        builder.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION_COLOR);

                        float alpha = ((DebuggableBody) body).getOutlineAlpha();
                        Vector3f color = ((DebuggableBody) body).getOutlineColor();

                        Vector3f position = body.getPhysicsLocation(new Vector3f()).subtract(new Vector3f((float) camera.getPos().x, (float) camera.getPos().y, (float) camera.getPos().z));
                        RenderSystem.translatef(position.x, position.y, position.z);
                        RenderSystem.multMatrix(new Matrix4f(QuaternionHelper.bulletToMinecraft(body.getPhysicsRotation(new Quaternion()))));

                        while (buffer.hasRemaining()) {
                            builder.vertex(buffer.get(), buffer.get(), buffer.get()).color(color.x, color.y, color.z, alpha).next();
                        }

                        Tessellator.getInstance().draw();
                        RenderSystem.depthMask(true);
                        RenderSystem.enableTexture();
                        RenderSystem.popMatrix();
                    }
                }
            }
        }
    }
}
