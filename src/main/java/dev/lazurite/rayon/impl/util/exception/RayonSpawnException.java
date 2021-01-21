package dev.lazurite.rayon.impl.util.exception;

import dev.lazurite.rayon.api.builder.RigidBodyRegistry;
import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;

/**
 * A custom runtime exception thrown when the user attempts
 * to spawn an entity that isn't registered in {@link RigidBodyRegistry}.
 * @see RayonSpawnS2CPacket
 * @see RigidBodyRegistry
 */
public class RayonSpawnException extends RuntimeException {
    public RayonSpawnException(String message) {
        super(message);
    }
}
