package dev.lazurite.rayon.core.impl.bullet.thread.util;

import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

/**
 * A pausable interface for {@link PhysicsThread}. Mainly
 * created since {@link Minecraft} cannot be included
 * directly in the server environment.
 */
public interface Pausable {
    default boolean isPaused() {
        Boolean result = DistExecutor.<Boolean>safeCallWhenOn(Dist.CLIENT, () ->
                (DistExecutor.SafeCallable<Boolean>) () -> Minecraft.getInstance().isPaused());

        return Boolean.TRUE.equals(result);
    }
}
