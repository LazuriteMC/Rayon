package dev.lazurite.rayon.physics.helper;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.linearmath.Transform;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.rayon.physics.body.block.BlockRigidBody;
import dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity;
import dev.lazurite.rayon.physics.world.DebuggableDynamicsWorld;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class DebugHelper extends IDebugDraw {
    private final DebuggableDynamicsWorld physicsWorld;

    public DebugHelper(DebuggableDynamicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    @Environment(EnvType.CLIENT)
    public void renderWorld(double cameraX, double cameraY, double cameraZ, boolean blocks) {
        Vector3f camPos = new Vector3f((float) cameraX, (float) cameraY, (float) cameraZ);

        for (CollisionObject body : physicsWorld.getCollisionObjectArray()) {
            Transform trans = body.getWorldTransform(new Transform());

            Vector3f color;
            if (body instanceof DynamicBodyEntity) {
                color = new Vector3f(1.0f, 0.4f, 0); // orang
            } else if (body instanceof BlockRigidBody) {
                color = new Vector3f(0, 0, 1); // blu
                if (!blocks) continue;
            } else {
                color = new Vector3f(1, 1, 1); // whit
            }

            render(body, trans.getRotation(new Quat4f()), trans.origin, camPos, color);
        }
    }

    @Environment(EnvType.CLIENT)
    public void render(CollisionObject body, Quat4f orientation, Vector3f pos, Vector3f cameraPos, Vector3f color) {
        RenderSystem.pushMatrix();

        // Get the distance between the camera and the physics object
        pos.sub(cameraPos);
        RenderSystem.translatef(pos.x, pos.y, pos.z);

        // Rotate the physics object render by it's orientation
        Matrix4f newMat = new Matrix4f(QuaternionHelper.quat4fToQuaternion(orientation));
        RenderSystem.multMatrix(newMat);

        // Actually draw now
        physicsWorld.debugDrawObject(new Transform(), body.getCollisionShape(), color);

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
