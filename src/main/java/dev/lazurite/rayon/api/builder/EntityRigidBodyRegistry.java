package dev.lazurite.rayon.api.builder;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.builder.RigidBodyEntry;
import net.minecraft.entity.Entity;

/**
 * This where you can register your {@link RigidBodyEntry} after
 * it's creation using the {@link EntityRigidBodyBuilder}.
 *
 * @since 1.1.0
 * @see Rayon#registerEntityComponentFactories
 * @see EntityRigidBodyBuilder
 */
public interface EntityRigidBodyRegistry {
    static <E extends Entity> void register(RigidBodyEntry<E> entry) {
        Rayon.entries.add(entry);
    }
}
