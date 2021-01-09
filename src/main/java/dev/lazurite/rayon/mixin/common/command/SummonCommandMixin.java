package dev.lazurite.rayon.mixin.common.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.lazurite.rayon.physics.body.entity.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.vecmath.Quat4f;

/**
 * Since {@link EntityRigidBody} objects cancel out the use of {@link Entity#setPos},
 * this mixin calls {@link EntityRigidBody#setPosition} instead.
 * @see EntityRigidBody
 */
@Mixin(SummonCommand.class)
public class SummonCommandMixin {
    @Inject(
            method = "execute",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/EntityType;loadEntityWithPassengers(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/World;Ljava/util/function/Function;)Lnet/minecraft/entity/Entity;"
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void execute(ServerCommandSource source, Identifier entity, Vec3d pos, CompoundTag nbt, boolean initialize, CallbackInfoReturnable<Integer> info, CompoundTag tag, ServerWorld world, Entity entity2) throws CommandSyntaxException {
        if (EntityRigidBody.is(entity2)) {
            EntityRigidBody dynamicEntity = EntityRigidBody.get(entity2);
            dynamicEntity.setPosition(VectorHelper.vec3dToVector3f(pos.add(new Vec3d(0, -dynamicEntity.getOffset().y + 0.1, 0))));
            entity2.yaw = QuaternionHelper.getYaw(dynamicEntity.getOrientation(new Quat4f()));
        }
    }
}
