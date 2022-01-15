package dev.lazurite.rayon.impl.util.debug;


import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.lazurite.rayon.api.event.render.DebugRenderEvents;
import dev.lazurite.rayon.impl.bullet.collision.body.MinecraftRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
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

        space.getTerrainMap().values().forEach(terrain -> CollisionObjectDebugger.renderBody(terrain, builder, stack, cameraPos, tickDelta));
        space.getRigidBodiesByClass(ElementRigidBody.class).forEach(elementRigidBody -> CollisionObjectDebugger.renderBody(elementRigidBody, builder, stack, cameraPos, tickDelta));
        Tesselator.getInstance().end();
    }

    public static void renderBody(MinecraftRigidBody rigidBody, BufferBuilder builder, PoseStack stack, Vec3 cameraPos, float tickDelta) {
        final var position = rigidBody.isStatic() ?
                rigidBody.getPhysicsLocation(new Vector3f()) :
                ((ElementRigidBody) rigidBody).getFrame().getLocation(new Vector3f(), tickDelta);

        final var rotation = rigidBody.isStatic() ?
                rigidBody.getPhysicsRotation(new Quaternion()) :
                ((ElementRigidBody) rigidBody).getFrame().getRotation(new Quaternion(), tickDelta);

        renderShape(rigidBody.getMinecraftShape(), position, rotation, builder, stack, cameraPos, rigidBody.getOutlineColor(), 1.0f);
    }

    public static void renderShape(MinecraftShape shape, Vector3f position, Quaternion rotation, BufferBuilder builder, PoseStack stack, Vec3 cameraPos, Vector3f color, float alpha) {
        final var triangles = shape.getTriangles(Quaternion.IDENTITY);
        final var positionMinusCamera = new Vector3f(position).subtract(Convert.toBullet(cameraPos));

        for (var triangle : triangles) {
            final var vertices = triangle.getVertices();

            stack.pushPose();
            stack.translate(positionMinusCamera.x, positionMinusCamera.y, positionMinusCamera.z);
            stack.mulPose(Convert.toMinecraft(rotation));
            final var p1 = vertices[0];
            final var p2 = vertices[1];
            final var p3 = vertices[2];

            builder.vertex(stack.last().pose(), p1.x, p1.y, p1.z).color(color.x, color.y, color.z, alpha).endVertex();
            builder.vertex(stack.last().pose(), p2.x, p2.y, p2.z).color(color.x, color.y, color.z, alpha).endVertex();
            builder.vertex(stack.last().pose(), p3.x, p3.y, p3.z).color(color.x, color.y, color.z, alpha).endVertex();
            builder.vertex(stack.last().pose(), p1.x, p1.y, p1.z).color(color.x, color.y, color.z, alpha).endVertex();
            stack.popPose();
        }
    }
}