package dev.lazurite.rayon.impl.physics.manager;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.DebugShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.mixin.client.KeyboardMixin;
import dev.lazurite.rayon.impl.mixin.client.render.DebugRendererMixin;
import dev.lazurite.rayon.impl.physics.body.BlockRigidBody;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.body.type.DebuggableBody;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

/**
 * This class handles basically everything related to debug rendering on
 * the client. The idea is that when the user presses F3+r, it triggers a
 * series of "layer changes" in this class. Each {@link DebugLayer} renders
 * something new on the screen in addition to the previous layer's contents.<br>
 * The two {@link DebugLayer}s currently available are {@link DebugLayer#ENTITY}
 * and {@link DebugLayer#BLOCK}. Since both {@link EntityRigidBody} and
 * {@link BlockRigidBody} are {@link DebuggableBody}s, they can both be rendered
 * to the screen as debug objects with their own respective layers and colors.
 *
 * @see Config
 * @see DrawMode
 * @see DebugLayer
 * @see KeyboardMixin
 * @see DebugRendererMixin
 */
@Environment(EnvType.CLIENT)
public final class DebugManager {
    private static final DebugManager instance = new DebugManager();

    private DebugLayer debugLayer = DebugLayer.ENTITY;
    private boolean enabled = false;

    public static DebugManager getInstance() {
        return instance;
    }

    private DebugManager() {
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Go to the next layer based on the {@link DebugLayer} enum ordinal value.
     * @return the current (and newly reached) {@link DebugLayer}.
     */
    public DebugLayer nextLayer() {
        if (enabled) {
            if (debugLayer.ordinal() + 1 >= DebugLayer.values().length) {
                enabled = false;
                debugLayer = DebugLayer.ENTITY;
            } else {
                debugLayer = DebugLayer.values()[debugLayer.ordinal() + 1];
            }
        } else {
            enabled = true;
        }

        return this.debugLayer;
    }

    public void render() {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        World world = MinecraftClient.getInstance().world;

        if (isEnabled()) {
            for (PhysicsRigidBody body : Rayon.WORLD.get(world).getRigidBodyList()) {
                if (body instanceof DebuggableBody) {
                    if (((DebuggableBody) body).getDebugLayer().ordinal() <= debugLayer.ordinal()) {
                        if (VectorHelper.vector3fToVec3d(body.getPhysicsLocation(new Vector3f())).distanceTo(camera.getPos()) < Config.getInstance().getLocal().getDebugDistance()) {
                            RenderSystem.pushMatrix();
                            RenderSystem.disableTexture();
                            RenderSystem.depthMask(false);
                            RenderSystem.lineWidth(1.0F);

                            FloatBuffer buffer = DebugShapeFactory.getDebugTriangles(body.getCollisionShape(), 1).rewind();
                            BufferBuilder builder = Tessellator.getInstance().getBuffer();
                            float alpha = ((DebuggableBody) body).getOutlineAlpha();
                            Vector3f color = ((DebuggableBody) body).getOutlineColor();

                            Vector3f position = body.getPhysicsLocation(new Vector3f())
                                    .subtract(VectorHelper.vec3dToVector3f(camera.getPos()));

                            builder.begin(Config.getInstance().getLocal().getDebugDrawMode().getMode(), VertexFormats.POSITION_COLOR);
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

    public enum DebugLayer {
        ENTITY("debug.rayon.layer.entity"),
        BLOCK("debug.rayon.layer.block");

        private final String translation;

        DebugLayer(String translation) {
            this.translation = translation;
        }

        public String getTranslation() {
            return this.translation;
        }
    }

    public enum DrawMode {
        LINES("debug.rayon.draw.lines", GL11.GL_LINE_LOOP),
        TRIANGLES("debug.rayon.draw.triangles", GL11.GL_TRIANGLES);

        private final String translation;
        private final int mode;

        DrawMode(String translation, int mode) {
            this.translation = translation;
            this.mode = mode;
        }

        public int getMode() {
            return this.mode;
        }

        public String getTranslation() {
            return this.translation;
        }
    }
}
