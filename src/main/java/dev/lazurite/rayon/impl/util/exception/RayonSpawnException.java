package dev.lazurite.rayon.impl.util.exception;

import dev.lazurite.rayon.impl.builder.RigidBodyRegistryImpl;
import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;

/**
 * A custom runtime exception thrown when the user attempts
 * to spawn an entity that isn't registered in {@link RigidBodyRegistryImpl}.
 * @see RayonSpawnS2CPacket
 */
public class RayonSpawnException extends RuntimeException {
    public RayonSpawnException(String message) {
        super(message);
    }
}
