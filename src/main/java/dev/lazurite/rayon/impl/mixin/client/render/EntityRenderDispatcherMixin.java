package dev.lazurite.rayon.impl.mixin.client.render;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow @Final public GameOptions gameOptions;
    @Shadow private boolean renderHitboxes;
    @Shadow private boolean renderShadows;
    @Shadow public Camera camera;
    @Shadow private World world;

    @Shadow public abstract <T extends Entity> EntityRenderer<? super T> getRenderer(T entity);
    @Shadow protected abstract void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity);
    @Shadow public abstract double getSquaredDistanceToCamera(double x, double y, double z);
    @Shadow protected abstract void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta);
    @Shadow private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius) { }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (EntityRigidBody.is(entity)) {
            EntityRenderer entityRenderer = getRenderer(entity);
            EntityRigidBody body = Rayon.ENTITY.get(entity);
            Quaternion rot = QuaternionHelper.bulletToMinecraft(body.getPhysicsRotation(new com.jme3.math.Quaternion(), tickDelta));
            Vec3d pos = VectorHelper.vector3fToVec3d(body.getPhysicsLocation(new Vector3f(), tickDelta)).subtract(camera.getPos());
            Vector3f bounds = body.getCollisionShape().boundingBox(new Vector3f(), new com.jme3.math.Quaternion(), new BoundingBox()).getExtent(new Vector3f()).multLocal(-0.5f);

            try {
                matrices.push();
                matrices.translate(pos.x, pos.y, pos.z);
                matrices.multiply(rot);

                matrices.translate(bounds.x, bounds.y, bounds.z);
                entityRenderer.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
                matrices.translate(-bounds.x, -bounds.y, -bounds.z);

                rot.conjugate();
                matrices.multiply(rot);

                if (entity.doesRenderOnFire()) {
                    renderFire(matrices, vertexConsumers, entity);
                }

                if (gameOptions.entityShadows && renderShadows && ((EntityRendererAccess) entityRenderer).getShadowRadius() > 0.0F && !entity.isInvisible()) {
                    double g = getSquaredDistanceToCamera(entity.getX(), entity.getY(), entity.getZ());
                    float h = (float) ((1.0D - g / 256.0D) * (double) ((EntityRendererAccess) entityRenderer).getShadowOpacity());
                    if (h > 0.0F) {
                        renderShadow(matrices, vertexConsumers, entity, h, tickDelta, world, ((EntityRendererAccess) entityRenderer).getShadowRadius());
                    }
                }

                if (renderHitboxes && !entity.isInvisible() && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
                    renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity, tickDelta);
                }

                matrices.pop();
            } catch (Throwable var24) {
                CrashReport crashReport = CrashReport.create(var24, "Rendering rigid body in world");
                CrashReportSection crashReportSection = crashReport.addElement("Rigid body being rendered");
                entity.populateCrashReport(crashReportSection);
                CrashReportSection crashReportSection2 = crashReport.addElement("Renderer details");
                crashReportSection2.add("Assigned renderer", entityRenderer);
                crashReportSection2.add("Location", "x: " + pos.getX() + ", y: " + pos.getY() + ", z: " + pos.getZ());
                crashReportSection2.add("Rotation", "x: " + rot.getX() + ", y: " + rot.getY() + ", z: " + pos.getZ() + ", w: " + rot.getW());
                crashReportSection2.add("Delta", tickDelta);
                throw new CrashException(crashReport);
            }

            info.cancel();
        }
    }
}
