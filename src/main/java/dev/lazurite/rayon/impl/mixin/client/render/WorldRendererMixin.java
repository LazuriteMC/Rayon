package dev.lazurite.rayon.impl.mixin.client.render;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * This mixin allows the entity to be rendered in the correct location by
 * replacing the position of the entity with the position of the rigid body
 * (which is slightly different).
 */
@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public class WorldRendererMixin {
    @ModifyArgs(
            method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
            )
    )
    public void render(Args args) {
        if (args.get(0) instanceof PhysicsElement) {
            Vector3f location = ((PhysicsElement) args.get(0)).getPhysicsLocation(new Vector3f(), args.get(5));
            Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
            args.set(1, location.x - cameraPos.x);
            args.set(2, location.y - cameraPos.y);
            args.set(3, location.z - cameraPos.z);
        }
    }
}
