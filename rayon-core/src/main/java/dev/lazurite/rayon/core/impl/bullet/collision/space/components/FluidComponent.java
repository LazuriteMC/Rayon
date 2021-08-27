package dev.lazurite.rayon.core.impl.bullet.collision.space.components;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.collision.body.MinecraftRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;

/**
 * @see MinecraftSpace
 */
public class FluidComponent {
    public static final float AIR_DENSITY = 1.2f;
    public static final float WATER_DENSITY = 997f;
    public static final float LAVA_DENSITY = 3100f;

    public static void step(MinecraftSpace space) {
        final var world = space.getWorld();

        /* For now, there is water at height = 3 everywhere. */
        final var waterHeight = 3;

        for (var rigidBody : space.getRigidBodiesByClass(MinecraftRigidBody.class)) {
            final var position = rigidBody.getPhysicsLocation(new Vector3f());
            final var volume = rigidBody.getCollisionShape().getVolume();

            final var rotation = rigidBody.getPhysicsRotation(new Quaternion());
            final var bb = rigidBody.getCollisionShape().copyHullVertices(position, rotation, new BoundingBox());
            System.out.println(bb.

            if (position.y < waterHeight) {
                var force = 9.81f * WATER_DENSITY * Math.abs(volume);
                rigidBody.applyCentralForce(new Vector3f(0, force, 0));
            }
        }
    }

    public static Vector3f getDragForceOn(MinecraftRigidBody rigidBody) {
        final var dragCoefficient = rigidBody.getDragCoefficient();
        final var area = (float) Math.pow(rigidBody.boundingBox(new BoundingBox()).getExtent(new Vector3f()).lengthSquared(), 2);
        final var k = (AIR_DENSITY * dragCoefficient * area) / 2.0f;
        return new Vector3f().set(rigidBody.getLinearVelocity(new Vector3f())).multLocal(-rigidBody.getLinearVelocity(new Vector3f()).lengthSquared()).multLocal(k);
    }

    static void doBuoyancyOn(MinecraftRigidBody rigidBody) {
        final var space = rigidBody.getSpace();
        final var location = rigidBody.getPhysicsLocation(new Vector3f());
        final var box = rigidBody.boundingBox(new BoundingBox());
    }
}
