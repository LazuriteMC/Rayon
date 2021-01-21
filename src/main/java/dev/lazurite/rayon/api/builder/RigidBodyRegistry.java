package dev.lazurite.rayon.api.builder;

import dev.lazurite.rayon.impl.builder.RigidBodyEntry;
import dev.lazurite.rayon.impl.builder.RigidBodyRegistryImpl;
import net.minecraft.entity.Entity;

import java.util.List;

public interface RigidBodyRegistry {
    static RigidBodyRegistry getInstance() {
        return RigidBodyRegistryImpl.INSTANCE;
    }

    <E extends Entity> void register(RigidBodyEntry<E> entry);
    List<RigidBodyEntry<? extends Entity>> get();
}
