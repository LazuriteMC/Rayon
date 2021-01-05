package dev.lazurite.rayon.mixin.common.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

/**
 * This mixin allows physics entities to be spawned using the summon command.
 * @author Ethan Johnson
 */
@Mixin(SummonCommand.class)
public class DynamicEntitySummon {
    /**
     * After the position is set for the summoned entity, this mixin checks to see if
     * the entity being summoned is a {@link PhysicsEntity} and sets it's {@link com.bulletphysics.dynamics.RigidBody}
     * position and also it's yaw rotation.
     * @param source the command source
     * @param entity the identifier of the entity to be spawned
     * @param pos the position to spawn at
     * @param nbt the compound tag for the entity
     * @param initialize
     * @param info required by every mixin injection
     * @param tag a local capture variable
     * @param world a local capture variable
     * @param entity2 a local capture variable
     * @throws CommandSyntaxException
     */
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
        /*if (entity2 instanceof PhysicsEntity) {
            PhysicsEntity physicsEntity = (PhysicsEntity) entity2;
            Vector3f position = new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);
            int id = source.getPlayer().getEntityId();
            float yaw = source.getPlayer().yaw;

            physicsEntity.setYaw(yaw);
            physicsEntity.getPhysics().setPosition(position);
            physicsEntity.setValue(PhysicsEntity.PLAYER_ID, id);
        }*/
    }
}
