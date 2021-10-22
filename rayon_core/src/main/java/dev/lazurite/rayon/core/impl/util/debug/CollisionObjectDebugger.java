package dev.lazurite.rayon.core.impl.util.debug;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.lazurite.rayon.core.api.event.render.DebugRenderEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.body.TerrainObject;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.core.impl.mixin.client.KeyboardMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

/**
 * This class handles debug rendering on the client. Press F3+r to render
 * all {@link ElementRigidBody} objects present in the {@link MinecraftSpace}.
 * @see KeyboardMixin
 */
public final class CollisionObjectDebugger {
    private static final CollisionObjectDebugger instance = new CollisionObjectDebugger();
    private boolean enabled;

    public static CollisionObjectDebugger getInstance() {
        return instance;
    }

    private CollisionObjectDebugger() {}

    public boolean toggle() {
        this.enabled = !this.enabled;
        return this.enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void renderSpace(MinecraftSpace space, float tickDelta, PoseStack poseStack) {
        final var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        final var builder = Tesselator.getInstance().getBuilder();
        final var stack = RenderSystem.getModelViewStack();
        stack.pushPose();
        stack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        MinecraftForge.EVENT_BUS.post(new DebugRenderEvents(space, builder, stack, cameraPos, tickDelta));
        builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        space.getTerrainObjects().stream().map(TerrainObject::getCollisionObject).forEach(
                physicsCollisionObject -> this.renderBody(physicsCollisionObject, builder, stack, cameraPos, tickDelta)
        );

        space.getRigidBodiesByClass(ElementRigidBody.class).forEach(
                elementRigidBody -> this.renderBody(elementRigidBody, builder, stack, cameraPos, tickDelta)
        );


        stack.popPose();
        RenderSystem.applyModelViewMatrix();
        Tesselator.getInstance().end();
    }

    private void renderBody(PhysicsCollisionObject body, BufferBuilder builder, PoseStack stack, Vec3 cameraPos, float tickDelta) {
        if (body instanceof Debuggable debuggable) {
            var points = ((MinecraftShape) body.getCollisionShape()).copyHullVertices();
            var color = debuggable.getOutlineColor();
            var alpha = debuggable.getOutlineAlpha();

            var position = body.isStatic() ?
                    body.getPhysicsLocation(new Vector3f()).subtract(Convert.toBullet(cameraPos)) :
                    ((ElementRigidBody) body).getFrame().getLocation(new Vector3f(), tickDelta).subtract(Convert.toBullet(cameraPos));

            var rotation = body.isStatic() ?
                    body.getPhysicsRotation(new Quaternion()) :
                    ((ElementRigidBody) body).getFrame().getRotation(new Quaternion(), tickDelta);

            stack.pushPose();
            stack.translate(position.x, position.y, position.z);
            stack.mulPose(Convert.toMinecraft(rotation));

            for (int i = 0; i < points.length; i += 3) {
                for (int j = 0; j < points.length; j += 3) {

                    var xSame = points[i] == points[j];
                    var ySame = points[i + 1] == points[j + 1];
                    var zSame = points[i + 2] == points[j + 2];

                    // the following checks to see if the i point and the j point share two axes
                    // that is, the i point can become the j point by changing one axis value
                    // this likely doesn't work with non-rectangular shapes
                    if (!(xSame && ySame && zSame) && ( (xSame && ySame) || (xSame && zSame) || (ySame && zSame) )) {
                        builder.vertex(stack.last().pose(), points[i], points[i + 1], points[i + 2])
                                .color(color.x, color.y, color.z, alpha)
                                .endVertex();

                        builder.vertex(stack.last().pose(), points[j], points[j + 1], points[j + 2])
                                .color(color.x, color.y, color.z, alpha)
                                .endVertex();
                    }
                }
            }

            stack.popPose();
        }
    }

}