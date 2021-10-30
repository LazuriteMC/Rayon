package dev.lazurite.rayon.core.api.event.collision;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.collision.body.TerrainObject;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Terrain Collision:</b> Element on Terrain</li>
 *     <li><b>Element Collision:</b> Element on Element</li>
 * </ul>
 * @see MinecraftSpace#collision
 * @since 1.0.0
 */
public class ElementCollisionEvents {
    public static final Event<TerrainCollision> TERRAIN_COLLISION = EventFactory.createArrayBacked(TerrainCollision.class, (callbacks) -> (element, terrainObject, impulse) -> {
        for (TerrainCollision event : callbacks) {
            event.onCollide(element, terrainObject, impulse);
        }
    });

    public static final Event<ElementCollision> ELEMENT_COLLISION = EventFactory.createArrayBacked(ElementCollision.class, (callbacks) -> (element1, element2, impulse) -> {
        for (ElementCollision event : callbacks) {
            event.onCollide(element1, element2, impulse);
        }
    });

    private ElementCollisionEvents() { }

    @FunctionalInterface
    public interface TerrainCollision {
        void onCollide(PhysicsElement element, TerrainObject terrainObject, float impulse);
    }

    @FunctionalInterface
    public interface ElementCollision {
        void onCollide(PhysicsElement element1, PhysicsElement element2, float impulse);
    }
}
