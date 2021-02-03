package dev.lazurite.rayon.api.packet;

import dev.lazurite.rayon.impl.util.spawn.RayonSpawnProvider;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;

/**
 * This custom spawn packet can only be used with physics entities. It isn't required in order to
 * spawn your custom entity but it is highly recommended since it handles the transfer of data such
 * as position, orientation, velocity, etc.<br><br>
 *
 * To use this, just call {@link RayonSpawnS2CPacket#get} within your {@link Entity#createSpawnPacket()} method.
 * @since 1.0.0
 */
public interface RayonSpawnS2CPacket {
    static Packet<?> get(Entity entity) {
        return RayonSpawnProvider.get(entity);
    }
}