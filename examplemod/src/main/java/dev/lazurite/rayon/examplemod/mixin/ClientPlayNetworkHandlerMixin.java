package dev.lazurite.rayon.examplemod.mixin;

import dev.lazurite.rayon.examplemod.ExampleMod;
import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
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

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow private ClientWorld world;

    @Inject(
            method = "onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;getEntityTypeId()Lnet/minecraft/entity/EntityType;"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo info, double x, double y, double z, EntityType<?> type) {
        Entity entity = null;

        if (type == ExampleMod.RECTANGULAR_PRISM_ENTITY) {
            entity = new RectangularPrismEntity(ExampleMod.RECTANGULAR_PRISM_ENTITY, world);
        }

        if (entity != null) {
            int i = packet.getId();
            entity.updatePositionAndAngles(x, y, z, (float) packet.getY() * 360 / 250, 0);
            entity.setEntityId(i);
            entity.setUuid(packet.getUuid());
            this.world.addEntity(i, entity);
            info.cancel();
        }
    }
}
