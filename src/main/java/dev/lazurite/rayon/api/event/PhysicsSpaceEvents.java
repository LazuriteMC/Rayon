package dev.lazurite.rayon.api.event;

import dev.lazurite.rayon.impl.bullet.thread.MinecraftSpace;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class PhysicsSpaceEvents {
    public static final Event<Step> STEP = EventFactory.createArrayBacked(Step.class, (callbacks) -> (space) -> {
        for (Step event : callbacks) {
            event.onStep(space);
        }
    });

    private PhysicsSpaceEvents() { }

    @FunctionalInterface
    public interface Step {
        void onStep(MinecraftSpace space);
    }
}
