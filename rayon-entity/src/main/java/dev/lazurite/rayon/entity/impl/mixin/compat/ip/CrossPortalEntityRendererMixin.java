package dev.lazurite.rayon.entity.impl.mixin.compat.ip;

import com.qouteall.immersive_portals.McHelper;
import com.qouteall.immersive_portals.render.CrossPortalEntityRenderer;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Environment(EnvType.CLIENT)
@Mixin(CrossPortalEntityRenderer.class)
public class CrossPortalEntityRendererMixin {
    @Redirect(
            method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/qouteall/immersive_portals/McHelper;setEyePos(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 0
            )
    )
    private static void setEyePos0(Entity entity, Vec3d eyePos, Vec3d lastTickEyePos) {
        if (!(entity instanceof EntityPhysicsElement)) {
            McHelper.setEyePos(entity, eyePos, lastTickEyePos);
        }
    }

    @Redirect(
            method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/qouteall/immersive_portals/McHelper;setEyePos(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 1
            )
    )
    private static void setEyePos1(Entity entity, Vec3d eyePos, Vec3d lastTickEyePos) {
        if (!(entity instanceof EntityPhysicsElement)) {
            McHelper.setEyePos(entity, eyePos, lastTickEyePos);
        }
    }
}
