package dev.lazurite.rayon.entity.impl.mixin.client;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * This mixin allows the entity to be rendered in the correct location by
 * replacing the position of the entity with the position of the rigid body
 * (which is slightly different).
 * @see EntityRenderDispatcherMixin
 */
@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @ModifyArgs(
            method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
            )
    )
    public void render(Args args) {
        if (args.get(0) instanceof EntityPhysicsElement) {
            var location = ((EntityPhysicsElement) args.get(0)).getPhysicsLocation(new Vector3f(), args.get(5));
            var cameraPos = this.client.gameRenderer.getCamera().getPos();
            args.set(1, location.x - cameraPos.x);
            args.set(2, location.y - cameraPos.y);
            args.set(3, location.z - cameraPos.z);
        }
    }
}
