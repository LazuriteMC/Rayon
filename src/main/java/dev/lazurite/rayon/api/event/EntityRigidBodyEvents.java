package dev.lazurite.rayon.api.event;

import dev.lazurite.rayon.impl.physics.body.BlockRigidBody;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Callbacks for {@link EntityRigidBody}. Includes:
 * <ul>
 *     <li>{@link EntityRigidBodyEvents#STEP}: Before each {@link EntityRigidBody#step}.</li>
 *     <li>{@link EntityRigidBodyEvents#LOAD}: Just before being added to the {@link MinecraftDynamicsWorld}.</li>
 *     <li>{@link EntityRigidBodyEvents#UNLOAD}: Just after being removed from the {@link MinecraftDynamicsWorld}.</li>
 *     <li>{@link EntityRigidBodyEvents#BLOCK_COLLISION}: Whenever contact is made between a {@link EntityRigidBody} and a {@link BlockRigidBody}.</li>
 *     <li>{@link EntityRigidBodyEvents#ENTITY_COLLISION}: Whenever contact is made between two {@link EntityRigidBody}s.</li>
 * </ul>
 *
 * @since 1.1.0
 * @see EntityRigidBody#step
 * @see MinecraftDynamicsWorld#collision
 */
public class EntityRigidBodyEvents {
    public static final Event<Step> STEP = EventFactory.createArrayBacked(Step.class, (callbacks) -> (body, delta) -> {
        for (Step event : callbacks) {
            event.onStep(body, delta);
        }
    });

    public static final Event<Load> LOAD = EventFactory.createArrayBacked(Load.class, (callbacks) -> (body, world) -> {
        for (Load event : callbacks) {
            event.onLoad(body, world);
        }
    });

    public static final Event<Unload> UNLOAD = EventFactory.createArrayBacked(Unload.class, (callbacks) -> (body, world) -> {
        for (Unload event : callbacks) {
            event.onUnload(body, world);
        }
    });

    public static final Event<BlockCollision> BLOCK_COLLISION = EventFactory.createArrayBacked(BlockCollision.class, (callbacks) -> (entityBody, blockBody) -> {
        for (BlockCollision event : callbacks) {
            event.onBlockCollision(entityBody, blockBody);
        }
    });

    public static final Event<EntityCollision> ENTITY_COLLISION = EventFactory.createArrayBacked(EntityCollision.class, (callbacks) -> (entityBody, otherEntityBody) -> {
        for (EntityCollision event : callbacks) {
            event.onEntityCollision(entityBody, otherEntityBody);
        }
    });

    private EntityRigidBodyEvents() { }

    @FunctionalInterface
    public interface Step {
        void onStep(EntityRigidBody body, float delta);
    }

    @FunctionalInterface
    public interface Load {
        void onLoad(EntityRigidBody body, MinecraftDynamicsWorld world);
    }

    @FunctionalInterface
    public interface Unload {
        void onUnload(EntityRigidBody body, MinecraftDynamicsWorld world);
    }

    @FunctionalInterface
    public interface BlockCollision {
        void onBlockCollision(EntityRigidBody entityBody, BlockRigidBody blockBody);
    }

    @FunctionalInterface
    public interface EntityCollision {
        void onEntityCollision(EntityRigidBody entityBody, EntityRigidBody otherEntityBody);
    }
}
