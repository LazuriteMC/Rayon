package dev.lazurite.rayon.api.event;

import com.jme3.bullet.PhysicsSpace;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.World;

/**
 * Callbacks for events within {@link MinecraftDynamicsWorld}. Includes:
 * <ul>
 *     <li>{@link DynamicsWorldEvents#START_STEP}: Before each {@link MinecraftDynamicsWorld#step}.</li>
 *     <li>{@link DynamicsWorldEvents#END_STEP}: After each {@link MinecraftDynamicsWorld#step}.</li>
 *     <li>{@link DynamicsWorldEvents#LOAD}: After {@link MinecraftDynamicsWorld#MinecraftDynamicsWorld(World, PhysicsSpace.BroadphaseType)}.</li>
 * </ul>
 *
 * @since 1.1.0
 * @see MinecraftDynamicsWorld#step
 * @see MinecraftDynamicsWorld#MinecraftDynamicsWorld(World, PhysicsSpace.BroadphaseType) 
 */
public final class DynamicsWorldEvents {
    public static final Event<StartStep> START_STEP = EventFactory.createArrayBacked(StartStep.class, (callbacks) -> (world, delta) -> {
        for (StartStep event : callbacks) {
            event.onStartStep(world, delta);
        }
    });

    public static final Event<EndStep> END_STEP = EventFactory.createArrayBacked(EndStep.class, (callbacks) -> (world, delta) -> {
        for (EndStep event : callbacks) {
            event.onEndStep(world, delta);
        }
    });

    public static final Event<Load> LOAD = EventFactory.createArrayBacked(Load.class, (callbacks) -> (world) -> {
        for (Load event : callbacks) {
            event.onLoad(world);
        }
    });

    private DynamicsWorldEvents() { }

    @FunctionalInterface
    public interface StartStep {
        void onStartStep(MinecraftDynamicsWorld world, float delta);
    }

    @FunctionalInterface
    public interface EndStep {
        void onEndStep(MinecraftDynamicsWorld world, float delta);
    }

    public interface Load {
        void onLoad(MinecraftDynamicsWorld world);
    }
}
