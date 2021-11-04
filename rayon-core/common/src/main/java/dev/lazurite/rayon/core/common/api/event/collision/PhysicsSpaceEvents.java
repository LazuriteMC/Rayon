package dev.lazurite.rayon.core.common.api.event.collision;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.lazurite.rayon.core.common.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.common.impl.bullet.collision.space.MinecraftSpace;

/**
 * @since 1.0.0
 */
public final class PhysicsSpaceEvents {
    public static final Event<Init> INIT = EventFactory.createLoop();

    public static final Event<Step> STEP = EventFactory.createLoop();

    public static final Event<ElementAdded> ELEMENT_ADDED = EventFactory.createLoop();

    public static final Event<ElementRemoved> ELEMENT_REMOVED = EventFactory.createLoop();

    private PhysicsSpaceEvents() { }

    @FunctionalInterface
    public interface Init {
        /**
         * Invoked each time a new {@link MinecraftSpace} is created.
         * @param space the minecraft space
         */
        void onInit(MinecraftSpace space);
    }

    @FunctionalInterface
    public interface Step {
        /**
         * Invoked each time the {@link MinecraftSpace} is stepped.
         * @param space the minecraft space
         */
        void onStep(MinecraftSpace space);
    }

    @FunctionalInterface
    public interface ElementAdded {
        /**
         * Invoked each time a new {@link ElementRigidBody} is added to the environment.
         * @param space the minecraft space
         * @param rigidBody the element rigid body being added
         */
        void onElementAdded(MinecraftSpace space, ElementRigidBody rigidBody);
    }

    @FunctionalInterface
    public interface ElementRemoved {
        /**
         * Invoked each time an {@link ElementRigidBody} is removed from the environment.
         * @param space the minecraft space
         * @param rigidBody the element rigid body being removed
         */
        void onElementRemoved(MinecraftSpace space, ElementRigidBody rigidBody);
    }
}