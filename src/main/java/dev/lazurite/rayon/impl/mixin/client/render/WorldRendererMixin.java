package dev.lazurite.rayon.impl.mixin.client.render;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * This mixin changes the given coordinates during entity rendering
 * so any {@link EntityRigidBody}s will be rendered at the rigid
 * body position instead of the entity position.
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @ModifyArgs(
            method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
            )
    )
    public void render(Args args) {
        if (Rayon.ENTITY.maybeGet(args.get(0)).isPresent()) {
            Vec3d pos = VectorHelper.vector3fToVec3d(Rayon.ENTITY.get(args.get(0)).getPhysicsLocation(new Vector3f(), args.get(5)))
                    .subtract(MinecraftClient.getInstance().gameRenderer.getCamera().getPos());

            args.set(1, pos.x);
            args.set(2, pos.y);
            args.set(3, pos.z);
        }
    }
}
