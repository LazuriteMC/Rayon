package dev.lazurite.rayon.core.impl.mixin.common;

import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.util.thread.ThreadStorage;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Ducks {@link ThreadStorage} into all {@link ReentrantThreadExecutor} objects.
 */
@Mixin(ReentrantThreadExecutor.class)
public class ReentrantThreadExecutorMixin implements ThreadStorage {
    @Unique private PhysicsThread thread;

    @Override
    public void setPhysicsThread(PhysicsThread thread) {
        this.thread = thread;
    }

    @Override
    public PhysicsThread getPhysicsThread() {
        return this.thread;
    }
}
