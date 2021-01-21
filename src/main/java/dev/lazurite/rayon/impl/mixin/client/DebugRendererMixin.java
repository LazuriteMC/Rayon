package dev.lazurite.rayon.impl.mixin.client;

import com.jme3.bullet.util.DebugShapeFactory;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.util.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (Config.getInstance().debug) {
            Rayon.WORLD.get(client.world).getRigidBodyList().forEach(body -> {
//                RenderSystem.disableTexture();
//                RenderSystem.depthMask(false);
//                RenderSystem.lineWidth(1.0F);
//
//                FloatBuffer buffer = DebugShapeFactory.getDebugTriangles(body.getCollisionShape(), 0);
//                VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.LINES);
//
//                int vbo = GL15.glGenBuffers();
//                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
//                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
//                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//
//                int vao = genVAO();
//                GL30.glBindVertexArray(vao);
//                GL30.glDrawElements(GL11.GL_TRIANGLES, 3, GL11.GL_UNSIGNED_BYTE, 0);
//
//                GL30.glBindVertexArray(0);
//
//                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
//                while (buffer.remaining() > 3) {
//                    consumer.vertex(buffer.get(), buffer.get(), buffer.get());
//                    bufferBuilder.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
//                    bufferBuilder.vertex(buffer.get(), buffer.get(), buffer.get()).color(255, 255, 0, 0.5F).next();
//                    Tessellator.getInstance().draw();
//                }
//
//                RenderSystem.depthMask(true);
//                RenderSystem.enableTexture();
            });
        }
    }

    @Unique
    private static int genVAO() {
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        return vao;
    }
}
