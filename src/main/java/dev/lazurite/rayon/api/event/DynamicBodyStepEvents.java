package dev.lazurite.rayon.api.event;

import dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Callbacks for when a {@link DynamicBodyEntity} on both the client and the
 * server calls their own {@link DynamicBodyEntity#step(float)} method.
 * There is one callback for the start of the loop and one for the end.
 * @see DynamicBodyEntity#step(float) 
 */
public final class DynamicBodyStepEvents {
    public static final Event<StartEntityStep> START_ENTITY_STEP = EventFactory.createArrayBacked(StartEntityStep.class, (callbacks) -> (dynamicEntity) -> {
        for (StartEntityStep event : callbacks) {
            event.onStartStep(dynamicEntity);
        }
    });

    public static final Event<EndEntityStep> END_ENTITY_STEP = EventFactory.createArrayBacked(EndEntityStep.class, (callbacks) -> (dynamicEntity) -> {
        for (EndEntityStep event : callbacks) {
            event.onEndStep(dynamicEntity);
        }
    });

    private DynamicBodyStepEvents() {
    }

    @FunctionalInterface
    public interface StartEntityStep {
        void onStartStep(DynamicBodyEntity dynamicEntity);
    }

    @FunctionalInterface
    public interface EndEntityStep {
        void onEndStep(DynamicBodyEntity dynamicEntity);
    }
}
