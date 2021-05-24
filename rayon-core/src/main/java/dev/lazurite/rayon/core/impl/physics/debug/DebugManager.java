package dev.lazurite.rayon.core.impl.physics.debug;

import com.jme3.bullet.util.DebugShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.rayon.core.impl.mixin.client.render.WorldRendererMixin;
import dev.lazurite.rayon.core.impl.physics.space.body.BlockRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.MinecraftRigidBody;
import dev.lazurite.rayon.core.impl.mixin.client.input.KeyboardMixin;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
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
 * {@link BlockRigidBody} are {@link MinecraftRigidBody}s, they can both be rendered
 * to the screen as debug objects with their own respective layers and colors.
 *
 * @see DebugLayer
 * @see KeyboardMixin
 * @see WorldRendererMixin
 */
@Environment(EnvType.CLIENT)
public final class DebugManager {
    private static final DebugManager instance = new DebugManager();

    private boolean enabled = false;

    public static DebugManager getInstance() {
        return instance;
    }

    private DebugManager() {
    }

    public boolean toggle() {
        this.enabled = !this.enabled;
        return this.enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void render(World world, MatrixStack matrices, Camera camera, float tickDelta) {
        MinecraftSpace.get(world).getRigidBodiesByClass(MinecraftRigidBody.class).forEach(rigidBody ->
            renderBody(rigidBody, matrices, camera, 2.0f, tickDelta, true));
    }

    public void renderBody(MinecraftRigidBody body, MatrixStack matrices, Camera camera, float lineWidth, float tickDelta, boolean translate) {
        FloatBuffer buffer = (FloatBuffer) DebugShapeFactory.getDebugTriangles(body.getCollisionShape(), 0).rewind();
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        Quaternion rotation;
        Vector3f position;

        if (body.isStatic()) {
            position = body.getPhysicsLocation(new Vector3f()).subtract(VectorHelper.vec3dToVector3f(camera.getPos()));
            rotation = body.getPhysicsRotation(new Quaternion());
        } else {
            position = body.getFrame().getLocation(new Vector3f(), tickDelta).subtract(VectorHelper.vec3dToVector3f(camera.getPos()));
            rotation = body.getFrame().getRotation(new Quaternion(), tickDelta);
        }

        Vector3f color = body.getOutlineColor();
        float alpha = body.getOutlineAlpha();

        matrices.push();
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(lineWidth);

        builder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        if (translate) matrices.translate(position.x, position.y, position.z);
        matrices.multiply(QuaternionHelper.bulletToMinecraft(rotation));

        while (buffer.hasRemaining()) {
            builder.vertex(matrices.peek().getModel(), buffer.get(), buffer.get(), buffer.get()).color(color.x, color.y, color.z, alpha).next();
        }

        Tessellator.getInstance().draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        matrices.pop();
    }
}
