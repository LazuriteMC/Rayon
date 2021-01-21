package dev.lazurite.rayon.api.event;

import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.BooleanSupplier;

/**
 * Callbacks for when the {@link MinecraftDynamicsWorld} on both the client and the
 * server calls the {@link MinecraftDynamicsWorld#step(BooleanSupplier)} method.
 * There is one callback for the start of the loop and one for the end.
 *
 * @since 1.0.0
 * @see MinecraftDynamicsWorld#step
 */
public final class DynamicsWorldStepEvents {
    public static final Event<StartWorldStep> START_WORLD_STEP = EventFactory.createArrayBacked(StartWorldStep.class, (callbacks) -> (dynamicsWorld, delta) -> {
        for (StartWorldStep event : callbacks) {
            event.onStartStep(dynamicsWorld, delta);
        }
    });

    public static final Event<EndWorldStep> END_WORLD_STEP = EventFactory.createArrayBacked(EndWorldStep.class, (callbacks) -> (dynamicsWorld, delta) -> {
        for (EndWorldStep event : callbacks) {
            event.onEndStep(dynamicsWorld, delta);
        }
    });

    private DynamicsWorldStepEvents() {
    }

    @FunctionalInterface
    public interface StartWorldStep {
        void onStartStep(MinecraftDynamicsWorld dynamicsWorld, float delta);
    }

    @FunctionalInterface
    public interface EndWorldStep {
        void onEndStep(MinecraftDynamicsWorld dynamicsWorld, float delta);
    }
}
