package dev.lazurite.rayon.side.client.render;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.linearmath.Transform;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.rayon.side.client.PhysicsWorld;
import dev.lazurite.rayon.helper.QuaternionHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

@Environment(EnvType.CLIENT)
public class DebugRenderer extends IDebugDraw {
    private final PhysicsWorld world;

    public DebugRenderer(PhysicsWorld world) {
        this.world = world;
    }

    public void renderWorld(double cameraX, double cameraY, double cameraZ, Vector3f color, boolean blocks) {
        Vector3f camPos = new Vector3f((float) cameraX, (float) cameraY, (float) cameraZ);

        if (blocks) {
            this.world.getRigidBodies().forEach(body -> {
                render(
                        body,
                        body.getOrientation(new Quat4f()),
                        body.getCenterOfMassPosition(new Vector3f()),
                        camPos,
                        color
                );
            });
        } else {
            this.world.getEntities().forEach(physics -> {
                render(
                        physics.getRigidBody(),
                        physics.getOrientation(),
                        physics.getPosition(),
                        camPos,
                        color
                );
            });
        }
    }

    public void render(RigidBody body, Quat4f orientation, Vector3f pos, Vector3f cameraPos, Vector3f color) {
        RenderSystem.pushMatrix();

        // Get the distance between the camera and the physics object
        pos.sub(cameraPos);
        RenderSystem.translatef(pos.x, pos.y, pos.z);

        // Rotate the physics object render by it's orientation
        Matrix4f newMat = new Matrix4f(QuaternionHelper.quat4fToQuaternion(orientation));
        RenderSystem.multMatrix(newMat);

        // Actually draw now
        world.debugDrawObject(new Transform(), body.getCollisionShape(), color);

        RenderSystem.popMatrix();
    }

    @Override
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
