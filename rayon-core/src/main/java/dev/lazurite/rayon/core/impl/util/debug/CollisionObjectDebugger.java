package dev.lazurite.rayon.core.impl.util.debug;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.rayon.core.api.event.render.DebugRenderEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.body.TerrainObject;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.core.impl.mixin.client.KeyboardMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles debug rendering on the client. Press F3+r to render
 * all {@link ElementRigidBody} objects present in the {@link MinecraftSpace}.
 * @see KeyboardMixin
 */
@Environment(EnvType.CLIENT)
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

    public void renderSpace(MinecraftSpace space, float tickDelta) {
        var cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        var builder = Tessellator.getInstance().getBuffer();
        var stack = new MatrixStack();

        builder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        DebugRenderEvents.BEFORE_RENDER.invoker().onRender(new DebugRenderEvents.Context(space, builder, stack, cameraPos, tickDelta));

        space.getTerrainObjects().stream().map(TerrainObject::getCollisionObject).forEach(
                physicsCollisionObject -> {
                    this.renderBody(physicsCollisionObject, builder, stack, cameraPos, tickDelta);
                }
        );

        space.getRigidBodiesByClass(ElementRigidBody.class).forEach(
                elementRigidBody -> {
                    this.renderBody(elementRigidBody, builder, stack, cameraPos, tickDelta);
                }
        );

        Tessellator.getInstance().draw();
    }

    private void renderBody(PhysicsCollisionObject body, BufferBuilder builder, MatrixStack stack, Vec3d cameraPos, float tickDelta) {
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

            stack.push();
            stack.translate(position.x, position.y, position.z);
            stack.multiply(Convert.toMinecraft(rotation));

            for (int i = 0; i < points.length; i += 3) {
                for (int j = 0; j < points.length; j += 3) {

                    var xSame = points[i] == points[j];
                    var ySame = points[i + 1] == points[j + 1];
                    var zSame = points[i + 2] == points[j + 2];

                    // the following checks to see if the i point and the j point share two axes
                    // that is, the i point can become the j point by changing one axis value
                    // this likely doesn't work with non-rectangular shapes
                    if (!(xSame && ySame && zSame) && ( (xSame && ySame) || (xSame && zSame) || (ySame && zSame) )) {
                        builder.vertex(stack.peek().getModel(), points[i], points[i + 1], points[i + 2])
                                .color(color.x, color.y, color.z, alpha)
                                .next();

                        builder.vertex(stack.peek().getModel(), points[j], points[j + 1], points[j + 2])
                                .color(color.x, color.y, color.z, alpha)
                                .next();
                    }
                }
            }

            stack.pop();
        }
    }

}