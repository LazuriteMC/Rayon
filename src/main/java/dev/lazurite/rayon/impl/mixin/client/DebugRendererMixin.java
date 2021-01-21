package dev.lazurite.rayon.impl.mixin.client;

import com.jme3.bullet.util.DebugShapeFactory;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.util.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (Config.INSTANCE.debug) {
            Rayon.WORLD.get(client.world).getRigidBodyList().forEach(body -> {
                RenderSystem.disableTexture();
                RenderSystem.depthMask(false);
                RenderSystem.lineWidth(1.0F);

                FloatBuffer buffer = DebugShapeFactory.debugVertices(body.getCollisionShape(), 0);
                float[] array = buffer.array();

                for (int i = 0; i < array.length; i += 3) {
                    vertexConsumers.getBuffer(RenderLayer.LINES).vertex(array[i], array[i+1], array[i+2]);
                }

//                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
//                bufferBuilder.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
//                bufferBuilder.vertex(from.x, from.y, from.z).color(color.x, color.y, color.z, 0.5F).next();
//                bufferBuilder.vertex(to.x, to.y, to.z).color(color.x, color.y, color.z, 0.5F).next();
//                Tessellator.getInstance().draw();

                RenderSystem.depthMask(true);
                RenderSystem.enableTexture();
            });
        }
    }
}
