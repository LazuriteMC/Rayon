package dev.lazurite.rayon.api.event;

import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Callbacks for when the {@link MinecraftDynamicsWorld} on both the client and the
 * server calls the {@link MinecraftDynamicsWorld#step} method.
 *
 * @since 1.1.0
 * @see MinecraftDynamicsWorld#step
 */
public final class DynamicsWorldStepEvents {
    public static final Event<StartWorldStep> START_WORLD_STEP = EventFactory.createArrayBacked(StartWorldStep.class, (callbacks) -> (world, delta) -> {
        for (StartWorldStep event : callbacks) {
            event.onStartStep(world, delta);
        }
    });

    public static final Event<EndWorldStep> END_WORLD_STEP = EventFactory.createArrayBacked(EndWorldStep.class, (callbacks) -> (world, delta) -> {
        for (EndWorldStep event : callbacks) {
            event.onEndStep(world, delta);
        }
    });

    public static final Event<WorldLoad> WORLD_LOAD = EventFactory.createArrayBacked(WorldLoad.class, (callbacks) -> (world) -> {
        for (WorldLoad event : callbacks) {
            event.onLoad(world);
        }
    });

    private DynamicsWorldStepEvents() { }

    @FunctionalInterface
    public interface StartWorldStep {
        void onStartStep(MinecraftDynamicsWorld world, float delta);
    }

    @FunctionalInterface
    public interface EndWorldStep {
        void onEndStep(MinecraftDynamicsWorld world, float delta);
    }

    public interface WorldLoad {
        void onLoad(MinecraftDynamicsWorld world);
    }
}
