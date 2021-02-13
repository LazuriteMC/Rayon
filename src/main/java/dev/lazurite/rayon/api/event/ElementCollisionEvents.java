package dev.lazurite.rayon.api.event;

import dev.lazurite.rayon.api.element.PhysicsElement;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElementCollisionEvents {
    public static final Event<BlockCollision> BLOCK_COLLISION = EventFactory.createArrayBacked(BlockCollision.class, (callbacks) -> (element, world, blockPos, blockState) -> {
        for (BlockCollision event : callbacks) {
            event.onCollide(element, world, blockPos, blockState);
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
        void onCollide(PhysicsElement element, World world, BlockPos blockPos, BlockState blockState);
    }

    @FunctionalInterface
    public interface ElementCollision {
        void onCollide(PhysicsElement element1, PhysicsElement element2);
    }
}
