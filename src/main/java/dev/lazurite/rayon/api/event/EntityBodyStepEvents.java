package dev.lazurite.rayon.api.event;

import dev.lazurite.rayon.physics.body.EntityRigidBody;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Callbacks for when a {@link EntityRigidBody} on both the client and the
 * server calls their own {@link EntityRigidBody#step(float)} method.
 * There is one callback for the start of the loop and one for the end.
 * @since 1.0.0
 * @see EntityRigidBody#step(float)
 */
public final class EntityBodyStepEvents {
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

    private EntityBodyStepEvents() {
    }

    @FunctionalInterface
    public interface StartEntityStep {
        void onStartStep(EntityRigidBody dynamicEntity);
    }

    @FunctionalInterface
    public interface EndEntityStep {
        void onEndStep(EntityRigidBody dynamicEntity);
    }
}
