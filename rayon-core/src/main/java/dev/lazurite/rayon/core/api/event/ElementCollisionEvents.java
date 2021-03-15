package dev.lazurite.rayon.core.api.event;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.space.MinecraftSpace;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Block Collision:</b> Element on Block</li>
 *     <li><b>Element Collision:</b> Element on Element</li>
 * </ul>
 * <b>Note:</b> All the events listed here run on the server thread or the client thread (not the physics thread).
 * @see MinecraftSpace#collision
 */
public class ElementCollisionEvents {
    public static final Event<BlockCollision> BLOCK_COLLISION = EventFactory.createArrayBacked(BlockCollision.class, (callbacks) -> (element, blockPos, blockState) -> {
        for (BlockCollision event : callbacks) {
            event.onCollide(element, blockPos, blockState);
        }
    });

    public static final Event<ElementCollision> ELEMENT_COLLISION = EventFactory.createArrayBacked(ElementCollision.class, (callbacks) -> (element1, element2) -> {
        for (ElementCollision event : callbacks) {
            event.onCollide(element1, element2);
        }
    });

    private ElementCollisionEvents() { }

    @FunctionalInterface
    public interface BlockCollision {
        void onCollide(PhysicsElement element, BlockPos blockPos, BlockState blockState);
    }

    @FunctionalInterface
    public interface ElementCollision {
        void onCollide(PhysicsElement element1, PhysicsElement element2);
    }
}
