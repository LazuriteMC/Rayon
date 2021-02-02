package dev.lazurite.rayon.api.event;

import com.jme3.bullet.PhysicsSpace;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.World;

/**
 * Callbacks for events within {@link MinecraftDynamicsWorld}. Includes:
 * <ul>
 *     <li>{@link DynamicsWorldEvents#START_WORLD_STEP}: Before each {@link MinecraftDynamicsWorld#step}.</li>
 *     <li>{@link DynamicsWorldEvents#END_WORLD_STEP}: After each {@link MinecraftDynamicsWorld#step}.</li>
 *     <li>{@link DynamicsWorldEvents#WORLD_LOAD}: After {@link MinecraftDynamicsWorld#MinecraftDynamicsWorld(World, PhysicsSpace.BroadphaseType)}.</li>
 * </ul>
 *
 * @since 1.1.0
 * @see MinecraftDynamicsWorld#step
 * @see MinecraftDynamicsWorld#MinecraftDynamicsWorld(World, PhysicsSpace.BroadphaseType) 
 */
public final class DynamicsWorldEvents {
    public static final Event<StartWorldStep> START_WORLD_STEP = EventFactory.createArrayBacked(StartWorldStep.class, (callbacks) -> (world, delta) -> {
        for (StartWorldStep event : callbacks) {
            event.onStartStep(world, delta);
        }
    });

    public static final Event<EndWorldStep> END_WORLD_STEP = EventFactory.createArrayBacked(EndWorldStep.class, (callbacks) -> (world, delta) -> {
        for (EndWorldStep event : callbacks) {
            event.onEndStep(world, delta);
        }
    });

    public static final Event<WorldLoad> WORLD_LOAD = EventFactory.createArrayBacked(WorldLoad.class, (callbacks) -> (world) -> {
        for (WorldLoad event : callbacks) {
            event.onLoad(world);
        }
    });

    private DynamicsWorldEvents() { }

    @FunctionalInterface
    public interface StartWorldStep {
        void onStartStep(MinecraftDynamicsWorld world, float delta);
    }

    @FunctionalInterface
    public interface EndWorldStep {
        void onEndStep(MinecraftDynamicsWorld world, float delta);
    }

    public interface WorldLoad {
        void onLoad(MinecraftDynamicsWorld world);
    }
}
