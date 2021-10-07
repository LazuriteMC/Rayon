package dev.lazurite.rayon.core.api.event.collision;

import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import net.minecraftforge.eventbus.api.Event;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Init:</b> Called just after the space is created</li>
 *     <li><b>Step:</b> Called each world step</li>
 *     <li><b>Element Added:</b> Called just before an element is added to the {@link MinecraftSpace}</li>
 *     <li><b>Element Removed:</b> Called just after an element is removed from the {@link MinecraftSpace}</li>
 * </ul>
 *
 * @since 1.0.0
 */
public abstract class PhysicsSpaceEvent extends Event {
    private final MinecraftSpace space;

    public PhysicsSpaceEvent(MinecraftSpace space) {
        this.space = space;
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }

    public static class Init extends PhysicsSpaceEvent {
        public Init(MinecraftSpace space) {
            super(space);
        }
    }

    public static class Step extends PhysicsSpaceEvent {
        public Step(MinecraftSpace space) {
            super(space);
        }
    }

    public static class ElementAdded extends PhysicsSpaceEvent {
        private final ElementRigidBody rigidBody;

        public ElementAdded(MinecraftSpace space, ElementRigidBody rigidBody) {
            super(space);
            this.rigidBody = rigidBody;
        }

        public ElementRigidBody getRigidBody() {
            return rigidBody;
        }
    }

    public static class ElementRemoved extends PhysicsSpaceEvent {
        private final ElementRigidBody rigidBody;

        public ElementRemoved(MinecraftSpace space, ElementRigidBody rigidBody) {
            super(space);
            this.rigidBody = rigidBody;
        }

        public ElementRigidBody getRigidBody() {
            return rigidBody;
        }
    }

}
