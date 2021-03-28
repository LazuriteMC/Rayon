package dev.lazurite.rayon.core.impl.physics.util.supplier;

import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.util.compat.ImmersiveWorldSupplier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This interface is what allows the {@link PhysicsThread} to retrieve
 * a list of {@link World} objects without knowing where they come from.
 * In this way, it can be used for client worlds or server worlds and,
 * in the case of immersive portals, it can be used to provide multiple
 * client worlds.
 * @see PhysicsThread
 * @see ClientWorldSupplier
 * @see ServerWorldSupplier
 * @see ImmersiveWorldSupplier
 */
public interface WorldSupplier {
     /**
      * Provides the complete list of {@link World}s. If
      * there aren't any, it will return an empty list.
      * @return the list of {@link World}s.
      */
     List<World> getWorlds();

     /**
      * Provides a specific {@link World} based on the given {@link RegistryKey}.
      * @param key the {@link RegistryKey} to identify the world with
      * @return the {@link World}
      */
     @Nullable World getWorld(RegistryKey<World> key);
}
