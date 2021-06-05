package dev.lazurite.rayon.core.api.event;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.BlockRigidBody;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Block Collision:</b> Element on Block</li>
 *     <li><b>Element Collision:</b> Element on Element</li>
 * </ul>
 * @see MinecraftSpace#collision
 * @since 1.0.0
 */
public class ElementCollisionEvents {
    public static final Event<BlockCollision> BLOCK_COLLISION = EventFactory.createArrayBacked(BlockCollision.class, (callbacks) -> (element, block, impulse) -> {
        for (BlockCollision event : callbacks) {
            event.onCollide(element, block, impulse);
        }
    });

    public static final Event<ElementCollision> ELEMENT_COLLISION = EventFactory.createArrayBacked(ElementCollision.class, (callbacks) -> (element1, element2, impulse) -> {
        for (ElementCollision event : callbacks) {
            event.onCollide(element1, element2, impulse);
        }
    });

    private ElementCollisionEvents() { }

    @FunctionalInterface
    public interface BlockCollision {
        void onCollide(PhysicsElement element, BlockRigidBody block, float impulse);
    }

    @FunctionalInterface
    public interface ElementCollision {
        void onCollide(PhysicsElement element1, PhysicsElement element2, float impulse);
    }
}
