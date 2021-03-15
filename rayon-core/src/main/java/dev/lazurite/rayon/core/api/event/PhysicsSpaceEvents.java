package dev.lazurite.rayon.core.api.event;

import dev.lazurite.rayon.core.impl.space.MinecraftSpace;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Step:</b> Called each world step</li>
 * </ul>
 * <b>Note:</b> all the events listed here run on the physics thread <i>only!</i>
 * @see MinecraftSpace#step()
 */
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
