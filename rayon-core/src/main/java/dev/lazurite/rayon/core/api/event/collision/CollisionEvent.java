package dev.lazurite.rayon.core.api.event.collision;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.collision.body.TerrainObject;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import net.minecraftforge.eventbus.api.Event;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Terrain Collision:</b> Element on Terrain</li>
 *     <li><b>Element Collision:</b> Element on Element</li>
 * </ul>
 *
 * @see MinecraftSpace#collision
 * @since 1.0.0
 */
public abstract class CollisionEvent extends Event {
    private final PhysicsElement elementA;
    private final float impulse;

    public CollisionEvent(PhysicsElement elementA, float impulse) {
        this.elementA = elementA;
        this.impulse = impulse;
    }

    public PhysicsElement getElementA() {
        return elementA;
    }

    public float getImpulse() {
        return impulse;
    }

    public static class TerrainCollisionEvent extends CollisionEvent {
        private final TerrainObject terrainObject;

        public TerrainCollisionEvent(PhysicsElement elementA, TerrainObject terrainObject, float impulse) {
            super(elementA, impulse);
            this.terrainObject = terrainObject;
        }

        public TerrainObject getTerrainObject() {
            return terrainObject;
        }
    }

    public static class ElementCollisionEvent extends CollisionEvent {
        private final PhysicsElement elementB;

        public ElementCollisionEvent(PhysicsElement elementA, PhysicsElement elementB, float impulse) {
            super(elementA, impulse);
            this.elementB = elementB;
        }

        public PhysicsElement getElementB() {
            return elementB;
        }
    }
}
