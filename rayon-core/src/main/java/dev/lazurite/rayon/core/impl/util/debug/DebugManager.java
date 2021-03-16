package dev.lazurite.rayon.core.impl.util.debug;

import com.google.common.collect.Lists;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.util.DebugShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.rayon.core.impl.thread.space.body.BlockRigidBody;
import dev.lazurite.rayon.core.impl.thread.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.thread.space.body.type.DebuggableBody;
import dev.lazurite.rayon.core.impl.mixin.client.input.KeyboardMixin;
import dev.lazurite.rayon.core.impl.mixin.client.render.DebugRendererMixin;
import dev.lazurite.rayon.core.impl.thread.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
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
import java.util.List;

/**
 * This class handles basically everything related to debug rendering on
 * the client. The idea is that when the user presses F3+r, it triggers a
 * series of "layer changes" in this class. Each {@link DebugLayer} renders
 * something new on the screen in addition to the previous layer's contents.<br>
 * The two {@link DebugLayer}s currently available are {@link DebugLayer#BODY}
 * and {@link DebugLayer#BLOCK}. Since both {@link ElementRigidBody} and
 * {@link BlockRigidBody} are {@link DebuggableBody}s, they can both be rendered
 * to the screen as debug objects with their own respective layers and colors.
 *
 * @see DebugLayer
 * @see KeyboardMixin
 * @see DebugRendererMixin
 */
@Environment(EnvType.CLIENT)
public final class DebugManager {
    private static final DebugManager instance = new DebugManager();

    private DebugLayer debugLayer = DebugLayer.BODY;
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
                debugLayer = DebugLayer.BODY;
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
            MinecraftSpace space = MinecraftSpace.get(world);
            List<PhysicsCollisionObject> collisionObjects = Lists.newArrayList();
            collisionObjects.addAll(space.getRigidBodyList());
            collisionObjects.addAll(space.getGhostObjectList());
            for (PhysicsCollisionObject body : collisionObjects) {
                if (body instanceof DebuggableBody) {
                    if (((DebuggableBody) body).getDebugLayer().ordinal() <= debugLayer.ordinal()) {
                        if (VectorHelper.vector3fToVec3d(body.getPhysicsLocation(new Vector3f()))
                                .distanceTo(camera.getPos()) < MinecraftClient.getInstance().options.viewDistance * 16) {
                            renderBody(body, VectorHelper.vec3dToVector3f(camera.getPos()));
                        }
                    }
                }
            }
        }
    }

    private void renderBody(PhysicsCollisionObject body, Vector3f cameraPos) {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(1.0F);

        FloatBuffer buffer = (FloatBuffer) DebugShapeFactory.getDebugTriangles(body.getCollisionShape(), 0).rewind();
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        float alpha = ((DebuggableBody) body).getOutlineAlpha();
        Vector3f color = ((DebuggableBody) body).getOutlineColor();

        Vector3f position = body.getPhysicsLocation(new Vector3f()).subtract(cameraPos);

        builder.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION_COLOR);
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
