package dev.lazurite.rayon.core.api.event;

import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.World;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>PreInit:</b> Called just before the space is created</li>
 *     <li><b>Init:</b> Called just after the space is created</li>
 *     <li><b>Step:</b> Called each world step</li>
 * </ul>
 */
public final class PhysicsSpaceEvents {
    public static final Event<PreInit> PREINIT = EventFactory.createArrayBacked(PreInit.class, (callbacks) -> (thread, world) -> {
        for (PreInit event : callbacks) {
            event.onPreInit(thread, world);
        }
    });

    public static final Event<Init> INIT = EventFactory.createArrayBacked(Init.class, (callbacks) -> (thread, space) -> {
        for (Init event : callbacks) {
            event.onInit(thread, space);
        }
    });

    public static final Event<Step> STEP = EventFactory.createArrayBacked(Step.class, (callbacks) -> (space) -> {
        for (Step event : callbacks) {
            event.onStep(space);
        }
    });

    private PhysicsSpaceEvents() { }

    @FunctionalInterface
    public interface PreInit {
        void onPreInit(PhysicsThread thread, World world);
    }

    @FunctionalInterface
    public interface Init {
        void onInit(PhysicsThread thread, MinecraftSpace space);
    }

    @FunctionalInterface
    public interface Step {
        void onStep(MinecraftSpace space);
    }
}
