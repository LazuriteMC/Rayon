package dev.lazurite.rayon.core.api.event;

import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Init:</b> Called just after the space is created</li>
 *     <li><b>Step:</b> Called each world step</li>
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

    private PhysicsSpaceEvents() { }

    @FunctionalInterface
    public interface Init {
        void onInit(MinecraftSpace space);
    }

    @FunctionalInterface
    public interface Step {
        void onStep(MinecraftSpace space);
    }
}
