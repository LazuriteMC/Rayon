package dev.lazurite.rayon.entity.impl.mixin.common;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.thread.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.RayonEntityCommon;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "createSpawnPacket", at = @At("HEAD"), cancellable = true)
    public void createSpawnPacket(CallbackInfoReturnable<Packet<?>> info) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeInt(entity.getEntityId());
            buf.writeUuid(entity.getUuid());
            buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
            VectorHelper.toBuffer(buf, rigidBody.getPhysicsLocation(new Vector3f()));
            VectorHelper.toBuffer(buf, rigidBody.getLinearVelocity(new Vector3f()));
            VectorHelper.toBuffer(buf, rigidBody.getAngularVelocity(new Vector3f()));
            QuaternionHelper.toBuffer(buf, rigidBody.getPhysicsRotation(new Quaternion()));

            info.setReturnValue(ServerPlayNetworking.createS2CPacket(new Identifier(RayonEntityCommon.MODID, "element_spawn_s2c"), buf));
        }
    }
}
