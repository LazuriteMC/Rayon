package dev.lazurite.rayon.entity_example.mixin;

import dev.lazurite.rayon.entity_example.server.ServerInitializer;
import dev.lazurite.rayon.entity_example.server.entity.TestEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.vecmath.Vector3f;
import java.util.UUID;

/**
 * This the class where entities are spawned on the client. Since the list of spawnable
 * entities is hard-coded in Minecraft's source code, we need to add the
 * {@link TestEntity} ourselves.
 * @author Ethan Johnson
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow ClientWorld world;

    /**
     * Necessary since the game all of the entity types hard-coded
     * into this method. This mixin just adds another one.
     * @param packet the spawn packet
     * @param info required by every mixin injection
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param type entity type
     */
    @Inject(
            method = "onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;getEntityTypeId()Lnet/minecraft/entity/EntityType;"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo info, double x, double y, double z, EntityType<?> type) {

        /* Create a new TestEntity and update its position, rotation, etc. */
        if (type == ServerInitializer.TEST_ENTITY) {
            TestEntity entity = new TestEntity(type, world);

            int i = packet.getId();
            UUID uuid = packet.getUuid();
            float yaw = (float) (packet.getYaw() * 360) / 256.0F;
            Vector3f pos = new Vector3f((float) x, (float) y, (float) z);

            entity.setEntityId(i);
            entity.setUuid(uuid);
            entity.updatePositionAndAngles(pos, yaw, 0);
            this.world.addEntity(i, entity);

            info.cancel();
        }
    }
}
