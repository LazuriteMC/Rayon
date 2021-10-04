package dev.lazurite.rayon.core.impl.bullet.collision.space.generator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;

import java.util.ArrayList;

public class PressureGenerator {
    public static void step(MinecraftSpace space) {
        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            var rigidBodyBox = Convert.toMinecraft(rigidBody.getCollisionShape().boundingBox(rigidBody.getPhysicsLocation(null), new Quaternion(), null));
            rigidBodyBox = rigidBodyBox.contract(rigidBodyBox.getXLength() * 0.2, rigidBodyBox.getYLength() * 0.2, rigidBodyBox.getZLength() * 0.2);

            var volume = 0.0f;

            final var gravity = -1.0f * space.getGravity(null).y;
            final var maxVolume = rigidBodyBox.getXLength() * rigidBodyBox.getYLength() * rigidBodyBox.getZLength();
            final var fluidObjects = rigidBody.getTerrainObjects().values().stream()
                    .filter(terrainObject -> terrainObject.getFluidState().isPresent()).toList();

            final var relativePoints = new ArrayList<Vector3f>();
            relativePoints.add(new Vector3f((float) rigidBodyBox.getXLength() * 0.5f, (float) rigidBodyBox.getYLength(), (float) (-1.0f * rigidBodyBox.getZLength() * 0.5f)));
            relativePoints.add(new Vector3f((float) (-1.0f * rigidBodyBox.getXLength() * 0.5f), (float) rigidBodyBox.getYLength(), (float) (-1.0f * rigidBodyBox.getZLength() * 0.5f)));
            relativePoints.add(new Vector3f((float) rigidBodyBox.getXLength() * 0.5f, (float) rigidBodyBox.getYLength(), (float) rigidBodyBox.getZLength() * 0.5f));
            relativePoints.add(new Vector3f((float) (-1.0f * rigidBodyBox.getXLength() * 0.5f), (float) rigidBodyBox.getYLength(), (float) rigidBodyBox.getZLength() * 0.5f));

            for (var fluidObject : fluidObjects) {
                final var fluidBox = Convert.toMinecraft(fluidObject.getCollisionObject().boundingBox(null));

                if (fluidBox.intersects(rigidBodyBox)) {
                    final var intersection = fluidBox.intersection(rigidBodyBox);
                    volume += intersection.getXLength() * intersection.getYLength() * intersection.getZLength();
                }
            }

            final var force = new Vector3f(0.0f, (float) (gravity * 1000 * Math.min(volume, maxVolume)), 0.0f).multLocal(0.25f);

            if (Float.isFinite(force.lengthSquared()) && force.lengthSquared() > 0.0f) {
                rigidBody.applyDragForce(1000);

                for (var point : relativePoints) {
                    rigidBody.applyForce(force, point);
                }
            } else {
                rigidBody.applyDragForce(1.2f);
            }
        }
    }
}
