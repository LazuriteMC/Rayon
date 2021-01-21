package dev.lazurite.rayon.impl.builder;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.api.builder.RigidBodyBuilder;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.api.builder.RigidBodyRegistry;
import net.minecraft.entity.Entity;

import java.util.List;

/**
 * The registry used for registering custom entities as dynamic entities.
 * @since 1.0.0
 * @see RigidBodyBuilder
 * @see Rayon#registerEntityComponentFactories
 */
public final class RigidBodyRegistryImpl implements RigidBodyRegistry {
    public static final RigidBodyRegistryImpl INSTANCE = new RigidBodyRegistryImpl();
    private final List<RigidBodyEntry<? extends Entity>> entries = Lists.newArrayList();

    private RigidBodyRegistryImpl() {
    }

    @Override
    public <E extends Entity> void register(RigidBodyEntry<E> entry) {
        entries.add(entry);
    }

    /**
     * Gets the list of entries.
     * @return a new list containing registered entities
     * @see Rayon#registerEntityComponentFactories 
     */
    @Override
    public List<RigidBodyEntry<? extends Entity>> get() {
        return Lists.newArrayList(entries);
    }
}
