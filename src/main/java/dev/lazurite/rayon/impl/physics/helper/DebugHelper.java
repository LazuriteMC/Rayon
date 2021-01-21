package dev.lazurite.rayon.impl.physics.helper;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.rayon.impl.physics.body.BlockRigidBody;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.world.DebuggableDynamicsWorld;
import dev.lazurite.rayon.impl.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

public class DebugHelper extends IDebugDraw {
    private final MinecraftDynamicsWorld dynamicsWorld;

    public DebugHelper(MinecraftDynamicsWorld physicsWorld) {
        this.dynamicsWorld = physicsWorld;
    }

    @Environment(EnvType.CLIENT)
    public void renderWorld(double cameraX, double cameraY, double cameraZ, boolean blocks) {
        Vector3f camPos = new Vector3f((float) cameraX, (float) cameraY, (float) cameraZ);

        for (PhysicsRigidBody body : dynamicsWorld.getRigidBodyList()) {
            Transform trans = body.getTransform(new Transform());

            Vector3f color;
            if (body instanceof EntityRigidBody) {
                color = new Vector3f(1.0f, 0.4f, 0); // orang
            } else if (body instanceof BlockRigidBody) {
                color = new Vector3f(0, 0, 1); // blu
                if (!blocks) continue;
            } else {
                color = new Vector3f(1, 1, 1); // whit
            }

            render(body, trans.getRotation(), trans.getTranslation(), camPos, color);
        }
    }

    @Environment(EnvType.CLIENT)
    public void render(PhysicsRigidBody body, Quaternion orientation, Vector3f pos, Vector3f cameraPos, Vector3f color) {
        RenderSystem.pushMatrix();

        // Get the distance between the camera and the physics object
        pos.subtract(cameraPos);
        RenderSystem.translatef(pos.x, pos.y, pos.z);

        // Rotate the physics object render by it's orientation
        Matrix4f newMat = new Matrix4f(QuaternionHelper.bulletToMinecraft(orientation));
        RenderSystem.multMatrix(newMat);

        // Actually draw now
        dynamicsWorld.debugDrawObject(new Transform(), body.getCollisionShape(), color);

        RenderSystem.popMatrix();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);

        RenderSystem.lineWidth(1.0F);

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(from.x, from.y, from.z).color(color.x, color.y, color.z, 0.5F).next();
        bufferBuilder.vertex(to.x, to.y, to.z).color(color.x, color.y, color.z, 0.5F).next();
        Tessellator.getInstance().draw();

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
    }

    @Override
    public void drawContactPoint(Vector3f PointOnB, Vector3f normalOnB, float distance, int lifeTime, Vector3f color) {

    }

    @Override
    public void reportErrorWarning(String warningString) {

    }

    @Override
    public void draw3dText(Vector3f location, String textString) {

    }

    @Override
    public void setDebugMode(int debugMode) {

    }

    @Override
    public int getDebugMode() {
        return 0;
    }
}
