package io.lazurite.api.mixin;

import io.lazurite.api.LazuriteAPI;
import io.lazurite.api.network.packet.ModdedServerS2C;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * This mixin class modifies the way sound works in minecraft. Normally, the distance to a sound is
 * calculated using the player's position and the source of the sound's position. When moving the camera, this
 * system doesn't work so well. Instead, the distance is now calculated between the source and the camera rather
 * than the player's actual position.
 * @author Ethan Johnson
 */
@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow @Final List<ServerPlayerEntity> players;

    /**
     * This method allows the server to tell the client that it is modded upon player connect.
     * @param connection
     * @param player
     * @param info required by every mixin injection
     */
    @Inject(at = @At("TAIL"), method = "onPlayerConnect")
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        ModdedServerS2C.send(player, LazuriteAPI.MODID);
    }

    /**
     * This mixin method recalculates the distance between the {@link ServerPlayerEntity} and the entity
     * which produced a sound by using the position of the drone if the player is moving the camera.
     * @param player the player
     * @param x the x position of the sound
     * @param y the y position of the sound
     * @param z the z position of the sound
     * @param distance the distance to the sound
     * @param worldKey the world key
     * @param packet the packet that will be sent
     * @param info required by every mixin injection
     */
    @Inject(method = "sendToAround", at = @At("TAIL"))
    public void sendToAround(PlayerEntity player, double x, double y, double z, double distance, RegistryKey<World> worldKey, Packet<?> packet, CallbackInfo info) {
        /*for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) this.players.get(i);

            if (serverPlayerEntity != player && serverPlayerEntity.world.getRegistryKey() == worldKey) {
                if(GogglesItem.isInGoggles(serverPlayerEntity)) {
                    QuadcopterEntity drone = (QuadcopterEntity) serverPlayerEntity.getCameraEntity();

                    double d = x - drone.getX();
                    double e = y - drone.getY();
                    double f = z - drone.getZ();
                    if (d * d + e * e + f * f < distance * distance) {
                        serverPlayerEntity.networkHandler.sendPacket(packet);
                    }
                }
            }
        }*/
    }
}
