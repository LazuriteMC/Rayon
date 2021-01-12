package dev.lazurite.rayon.api.event;

import dev.lazurite.rayon.physics.body.BlockRigidBody;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Callbacks for when a {@link EntityRigidBody} on both the client and the server
 * collides with either another {@link EntityRigidBody} or a {@link BlockRigidBody}.
 * @since 1.0.0
 * @see EntityRigidBody#step
 */
public final class EntityBodyCollisionEvent {
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

    private EntityBodyCollisionEvent() {
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
