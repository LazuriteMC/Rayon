package dev.lazurite.rayon.util.exception;

import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;

/**
 * A custom runtime exception thrown when the user attempts
 * to spawn an entity that isn't registered in {@link DynamicEntityRegistry}.
 * @see RayonSpawnS2CPacket
 */
public class RayonSpawnException extends RuntimeException {
    public RayonSpawnException(String message) {
        super(message);
    }
}
