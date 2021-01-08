package dev.lazurite.rayon.api.event;

import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class DynamicsWorldStepEvents {
    public static final Event<StartWorldStep> START_WORLD_STEP = EventFactory.createArrayBacked(StartWorldStep.class, (callbacks) -> (dynamicsWorld) -> {
        for (StartWorldStep event : callbacks) {
            event.onStartStep(dynamicsWorld);
        }
    });

    public static final Event<EndWorldStep> END_WORLD_STEP = EventFactory.createArrayBacked(EndWorldStep.class, (callbacks) -> (dynamicsWorld) -> {
        for (EndWorldStep event : callbacks) {
            event.onEndStep(dynamicsWorld);
        }
    });

    private DynamicsWorldStepEvents() {
    }

    @FunctionalInterface
    public interface StartWorldStep {
        void onStartStep(MinecraftDynamicsWorld dynamicsWorld);
    }

    @FunctionalInterface
    public interface EndWorldStep {
        void onEndStep(MinecraftDynamicsWorld dynamicsWorld);
    }
}
