package dev.lazurite.rayon.api.event;

import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Step:</b> Called each world step</li>
 *     <li><b>Load:</b> Called on world load</li>
 * </ul>
 * <b>Note:</b> all the events listed here run on the physics thread <i>only!</i>
 * @see MinecraftSpace#step()
 * @see PhysicsThread#run()
 */
public final class PhysicsSpaceEvents {
    public static final Event<Step> STEP = EventFactory.createArrayBacked(Step.class, (callbacks) -> (space) -> {
        for (Step event : callbacks) {
            event.onStep(space);
        }
    });

    public static final Event<Load> LOAD = EventFactory.createArrayBacked(Load.class, (callbacks) -> (space) -> {
        for (Load event : callbacks) {
            event.onLoad(space);
        }
    });

    private PhysicsSpaceEvents() { }

    @FunctionalInterface
    public interface Step {
        void onStep(MinecraftSpace space);
    }

    @FunctionalInterface
    public interface Load {
        void onLoad(MinecraftSpace space);
    }
}
