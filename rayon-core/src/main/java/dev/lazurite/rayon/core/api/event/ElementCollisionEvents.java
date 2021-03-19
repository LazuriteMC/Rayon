package dev.lazurite.rayon.core.api.event;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.thread.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.thread.space.body.BlockRigidBody;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.thread.ReentrantThreadExecutor;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Block Collision:</b> Element on Block</li>
 *     <li><b>Element Collision:</b> Element on Element</li>
 * </ul>
 * <b>Note:</b> All the events listed here run on the physics thread but include the client or server object to allow for thread changing.
 * @see MinecraftSpace#collision
 */
public class ElementCollisionEvents {
    public static final Event<BlockCollision> BLOCK_COLLISION = EventFactory.createArrayBacked(BlockCollision.class, (callbacks) -> (thread, element, block, impulse) -> {
        for (BlockCollision event : callbacks) {
            event.onCollide(thread, element, block, impulse);
        }
    });

    public static final Event<ElementCollision> ELEMENT_COLLISION = EventFactory.createArrayBacked(ElementCollision.class, (callbacks) -> (thread, element1, element2, impulse) -> {
        for (ElementCollision event : callbacks) {
            event.onCollide(thread, element1, element2, impulse);
        }
    });

    private ElementCollisionEvents() { }

    @FunctionalInterface
    public interface BlockCollision {
        void onCollide(ReentrantThreadExecutor<? extends Runnable> thread, PhysicsElement element, BlockRigidBody block, float impulse);
    }

    @FunctionalInterface
    public interface ElementCollision {
        void onCollide(ReentrantThreadExecutor<? extends Runnable> thread, PhysicsElement element1, PhysicsElement element2, float impulse);
    }
}
