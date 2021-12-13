package dev.lazurite.rayon.impl.util.debug;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.lazurite.rayon.api.event.render.DebugRenderEvents;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.terrain.Terrain;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;

/**
 * This class handles debug rendering on the client. Press F3+r to render
 * all {@link ElementRigidBody} objects present in the {@link MinecraftSpace}.
 */
@Environment(EnvType.CLIENT)
public final class CollisionObjectDebugger {
    private static boolean enabled;

    private CollisionObjectDebugger() {}

    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void renderSpace(MinecraftSpace space, PoseStack stack, float tickDelta) {
        final var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        final var builder = Tesselator.getInstance().getBuilder();

        DebugRenderEvents.BEFORE_RENDER.invoke(new DebugRenderEvents.Context(space, builder, stack, cameraPos, tickDelta));
        builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

//        space.getTerrainObjects().stream().map(Terrain::getCollisionObject).forEach(physicsCollisionObject -> CollisionObjectDebugger.renderBody(physicsCollisionObject, builder, stack, cameraPos, tickDelta));
        space.getRigidBodiesByClass(ElementRigidBody.class).forEach(elementRigidBody -> CollisionObjectDebugger.renderBody(elementRigidBody, builder, stack, cameraPos, tickDelta));
        Tesselator.getInstance().end();
    }

    public static void renderBody(PhysicsCollisionObject body, BufferBuilder builder, PoseStack stack, Vec3 cameraPos, float tickDelta) {
        if (body instanceof Debuggable debuggable) {
//            var points = ((MinecraftShape) body.getCollisionShape()).copyHullVertices();
            var points = ((MinecraftShape) body.getCollisionShape()).getTriangles();
            var color = debuggable.getOutlineColor();
            var alpha = debuggable.getOutlineAlpha();

            var position = body.isStatic() ?
                    body.getPhysicsLocation(new Vector3f()).subtract(Convert.toBullet(cameraPos)) :
                    ((ElementRigidBody) body).getFrame().getLocation(new Vector3f(), tickDelta).subtract(Convert.toBullet(cameraPos));

            var rotation = body.isStatic() ?
                    body.getPhysicsRotation(new Quaternion()) :
                    ((ElementRigidBody) body).getFrame().getRotation(new Quaternion(), tickDelta);


            for (int i = 0; i < points.size(); i += 3) {
                stack.pushPose();
                stack.translate(position.x, position.y, position.z);
                stack.mulPose(Convert.toMinecraft(rotation));
                final var p1 = points.get(i);
                final var p2 = points.get(i + 1);
                final var p3 = points.get(i + 2);

                builder.vertex(stack.last().pose(), p1.x, p1.y, p1.z).color(color.x, color.y, color.z, alpha).endVertex();
                builder.vertex(stack.last().pose(), p2.x, p2.y, p2.z).color(color.x, color.y, color.z, alpha).endVertex();
                builder.vertex(stack.last().pose(), p3.x, p3.y, p3.z).color(color.x, color.y, color.z, alpha).endVertex();
                builder.vertex(stack.last().pose(), p1.x, p1.y, p1.z).color(color.x, color.y, color.z, alpha).endVertex();
                stack.popPose();
            }

//            for (int i = 0; i < points.length; i += 3) {
//                for (int j = 0; j < points.length; j += 3) {
//
//                    var xSame = points[i] == points[j];
//                    var ySame = points[i + 1] == points[j + 1];
//                    var zSame = points[i + 2] == points[j + 2];
//
//                    // the following checks to see if the i point and the j point share two axes
//                    // that is, the i point can become the j point by changing one axis value
//                    // this likely doesn't work with non-rectangular shapes
//                    if (!(xSame && ySame && zSame) && ( (xSame && ySame) || (xSame && zSame) || (ySame && zSame) )) {
//                        builder.vertex(stack.last().pose(), points[i], points[i + 1], points[i + 2])
//                                .color(color.x, color.y, color.z, alpha)
//                                .endVertex();
//
//                        builder.vertex(stack.last().pose(), points[j], points[j + 1], points[j + 2])
//                                .color(color.x, color.y, color.z, alpha)
//                                .endVertex();
//                    }
//                }
//            }

        }
    }
}