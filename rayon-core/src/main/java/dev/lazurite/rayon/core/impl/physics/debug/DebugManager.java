package dev.lazurite.rayon.core.impl.physics.debug;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.DebugShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.rayon.core.impl.mixin.client.render.WorldRendererMixin;
import dev.lazurite.rayon.core.impl.physics.space.body.BlockRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.type.DebuggableBody;
import dev.lazurite.rayon.core.impl.mixin.client.input.KeyboardMixin;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
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
 * @see WorldRendererMixin
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

    public void render(World world, Camera camera, float tickDelta) {
        for (DebuggableBody body : MinecraftSpace.get(world).getRigidBodiesByClass(DebuggableBody.class)) {
            if (body instanceof PhysicsRigidBody && body.getDebugLayer().ordinal() <= debugLayer.ordinal()) {
                if (VectorHelper.vector3fToVec3d(((PhysicsRigidBody) body).getPhysicsLocation(new Vector3f()))
                        .distanceTo(camera.getPos()) < MinecraftClient.getInstance().options.viewDistance * 16) {
                    renderBody((PhysicsRigidBody) body, VectorHelper.vec3dToVector3f(camera.getPos()), tickDelta);
                }
            }
        }
    }

    private void renderBody(PhysicsRigidBody body, Vector3f cameraPos, float tickDelta) {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(1.0F);

        FloatBuffer buffer = (FloatBuffer) DebugShapeFactory.getDebugTriangles(body.getCollisionShape(), 0).rewind();
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        float alpha = ((DebuggableBody) body).getOutlineAlpha();
        Vector3f color = ((DebuggableBody) body).getOutlineColor();

        Vector3f position;
        Quaternion rotation;

        if (body instanceof ElementRigidBody) {
            position = ((ElementRigidBody) body).getElement().getPhysicsLocation(new Vector3f(), tickDelta);
            rotation = ((ElementRigidBody) body).getElement().getPhysicsRotation(new Quaternion(), tickDelta);
        } else {
            position = body.getPhysicsLocation(new Vector3f());
            rotation = body.getPhysicsRotation(new Quaternion());
        }

        position.set(position.subtract(cameraPos));

        builder.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION_COLOR);
        RenderSystem.translatef(position.x, position.y, position.z);
        RenderSystem.multMatrix(new Matrix4f(QuaternionHelper.bulletToMinecraft(rotation)));

        while (buffer.hasRemaining()) {
            builder.vertex(buffer.get(), buffer.get(), buffer.get()).color(color.x, color.y, color.z, alpha).next();
        }

        Tessellator.getInstance().draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}
