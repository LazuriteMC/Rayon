package dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.world;

import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

/**
 * This interface is what allows the {@link PhysicsThread} to retrieve
 * a list of {@link World} objects without knowing where they come from.
 * In this way, it can be used for client worlds or server worlds and,
 * in the case of immersive portals, it can be used to provide multiple
 * client worlds.
 * @see PhysicsThread
 * @see ClientWorldSupplier
 * @see ServerWorldSupplier
 */
public interface WorldSupplier {
     /**
      * Provides the complete list of {@link World}s. If
      * there aren't any, it will return an empty list.
      * @return the list of {@link World}s.
      */
     List<World> getAll();

     /**
      * Provides a specific {@link World} based on the given {@link RegistryKey}.
      * @param key the {@link RegistryKey} to identify the world with
      * @return a {@link World}
      */
     World get(RegistryKey<World> key);

     /**
      * Provides a specific {@link World} based on the given {@link RegistryKey}.
      * @param key the {@link RegistryKey} to identify the world with
      * @return an optional {@link World}
      */
     Optional<World> getOptional(RegistryKey<World> key);
}
