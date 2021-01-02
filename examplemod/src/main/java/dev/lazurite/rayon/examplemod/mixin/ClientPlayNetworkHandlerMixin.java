package dev.lazurite.rayon.examplemod.mixin;

import dev.lazurite.rayon.examplemod.ExampleMod;
import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import dev.lazurite.rayon.physics.entity.EntityRigidBody;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.vecmath.Vector3f;

/**
 * Contains mixins mostly relating to {@link Entity} spawning, movement, and positioning.
 * Also handles game join operations.
 * @author Ethan Johnson
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow ClientWorld world;

    /**
     * This mixin is necessary since the game hard codes all of the entity types into
     * this method. This mixin just adds another one.
     * @param packet
     * @param info required by every mixin injection
     * @param x
     * @param y
     * @param z
     * @param type
     */
    @Inject(
            method = "onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;getEntityTypeId()Lnet/minecraft/entity/EntityType;"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo info, double x, double y, double z, EntityType<?> type) {
        if (type == ExampleMod.RECTANGULAR_PRISM_ENTITY) {
            RectangularPrismEntity entity = new RectangularPrismEntity(type, world);

            int i = packet.getId();
            float yaw = ((float) packet.getYaw() * 360) / 256.0F;
            EntityRigidBody.get(entity).setPosition(new Vector3f((float) x, (float) y, (float) z));
            entity.setEntityId(i);
            entity.setUuid(packet.getUuid());
            this.world.addEntity(i, entity);
            info.cancel();
        }
    }
}
