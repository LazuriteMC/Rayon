package dev.lazurite.rayon.core.api.event.collision;

import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Init:</b> Called just after the space is created</li>
 *     <li><b>Step:</b> Called each world step</li>
 *     <li><b>Element Added:</b> Called just before an element is added to the {@link MinecraftSpace}</li>
 *     <li><b>Element Removed:</b> Called just after an element is removed from the {@link MinecraftSpace}</li>
 * </ul>
 * @since 1.0.0
 */
public final class PhysicsSpaceEvents {
    public static final Event<Init> INIT = EventFactory.createArrayBacked(Init.class, (callbacks) -> space -> {
        for (Init event : callbacks) {
            event.onInit(space);
        }
    });

    public static final Event<Step> STEP = EventFactory.createArrayBacked(Step.class, (callbacks) -> space -> {
        for (Step event : callbacks) {
            event.onStep(space);
        }
    });

    public static final Event<ElementAdded> ELEMENT_ADDED = EventFactory.createArrayBacked(ElementAdded.class, (callbacks) -> (space, rigidBody) -> {
        for (ElementAdded event : callbacks) {
            event.onElementAdded(space, rigidBody);
        }
    });

    public static final Event<ElementRemoved> ELEMENT_REMOVED = EventFactory.createArrayBacked(ElementRemoved.class, (callbacks) -> (space, rigidBody) -> {
        for (ElementRemoved event : callbacks) {
            event.onElementRemoved(space, rigidBody);
        }
    });

    private PhysicsSpaceEvents() { }

    @FunctionalInterface
    public interface Init {
        void onInit(MinecraftSpace space);
    }

    @FunctionalInterface
    public interface Step {
        void onStep(MinecraftSpace space);
    }

    @FunctionalInterface
    public interface ElementAdded {
        void onElementAdded(MinecraftSpace space, ElementRigidBody rigidBody);
    }

    @FunctionalInterface
    public interface ElementRemoved {
        void onElementRemoved(MinecraftSpace space, ElementRigidBody rigidBody);
    }
}
