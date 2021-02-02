package dev.lazurite.rayon.api.event;

import dev.lazurite.rayon.impl.physics.body.BlockRigidBody;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Callbacks for {@link EntityRigidBody}. Includes:
 * <ul>
 *     <li>{@link EntityRigidBodyEvents#START_ENTITY_BODY_STEP}: Before each {@link EntityRigidBody#step}.</li>
 *     <li>{@link EntityRigidBodyEvents#END_ENTITY_BODY_STEP}: After each {@link EntityRigidBody#step}.</li>
 *     <li>{@link EntityRigidBodyEvents#ENTITY_BODY_LOAD}: Just before being added to the {@link MinecraftDynamicsWorld}.</li>
 *     <li>{@link EntityRigidBodyEvents#ENTITY_BODY_UNLOAD}: Just after being removed from the {@link MinecraftDynamicsWorld}.</li>
 *     <li>{@link EntityRigidBodyEvents#BLOCK_COLLISION}: Whenever contact is made between a {@link EntityRigidBody} and a {@link BlockRigidBody}.</li>
 *     <li>{@link EntityRigidBodyEvents#ENTITY_COLLISION}: Whenever contact is made between two {@link EntityRigidBody}s.</li>
 * </ul>
 *
 * @since 1.1.0
 * @see EntityRigidBody#step
 * @see MinecraftDynamicsWorld#collision
 */
public class EntityRigidBodyEvents {
    public static final Event<StartEntityBodyStep> START_ENTITY_BODY_STEP = EventFactory.createArrayBacked(StartEntityBodyStep.class, (callbacks) -> (body, delta) -> {
        for (StartEntityBodyStep event : callbacks) {
            event.onStartStep(body, delta);
        }
    });

    public static final Event<EndEntityBodyStep> END_ENTITY_BODY_STEP = EventFactory.createArrayBacked(EndEntityBodyStep.class, (callbacks) -> (body, delta) -> {
        for (EndEntityBodyStep event : callbacks) {
            event.onEndStep(body, delta);
        }
    });

    public static final Event<EntityBodyLoad> ENTITY_BODY_LOAD = EventFactory.createArrayBacked(EntityBodyLoad.class, (callbacks) -> (body, world) -> {
        for (EntityBodyLoad event : callbacks) {
            event.onLoad(body, world);
        }
    });

    public static final Event<EntityBodyUnload> ENTITY_BODY_UNLOAD = EventFactory.createArrayBacked(EntityBodyUnload.class, (callbacks) -> (body, world) -> {
        for (EntityBodyUnload event : callbacks) {
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
    public interface StartEntityBodyStep {
        void onStartStep(EntityRigidBody body, float delta);
    }

    @FunctionalInterface
    public interface EndEntityBodyStep {
        void onEndStep(EntityRigidBody body, float delta);
    }

    @FunctionalInterface
    public interface EntityBodyLoad {
        void onLoad(EntityRigidBody body, MinecraftDynamicsWorld world);
    }

    @FunctionalInterface
    public interface EntityBodyUnload {
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
