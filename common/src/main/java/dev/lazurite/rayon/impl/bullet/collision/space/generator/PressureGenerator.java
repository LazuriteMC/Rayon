package dev.lazurite.rayon.impl.bullet.collision.space.generator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.Fluids;

public class PressureGenerator {
    public static final float WATER_DENSITY = 1000f;
    private static final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

    private static final Vector3f v1 = new Vector3f();
    private static final Vector3f v2 = new Vector3f();
    private static final Vector3f v3 = new Vector3f();
    private static final Vector3f v4 = new Vector3f();
    private static final Vector3f v5 = new Vector3f();
    private static final Vector3f v6 = new Vector3f();
    private static final Vector3f v7 = new Vector3f();
    private static final Vector3f v8 = new Vector3f();
    private static final Quaternion q1 = new Quaternion();

    public static void step(MinecraftSpace space) {
        final var gravity = space.getGravity(null);
        final var level = space.getLevel();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            final var location = rigidBody.getPhysicsLocation(v1);
            final var linearVelocity = rigidBody.getLinearVelocity(v2);
            final var angularVelocity = rigidBody.getAngularVelocity(v3);
            final var rotation = rigidBody.getPhysicsRotation(q1);
            final var triangles = rigidBody.getCollisionShape().getTriangles(rotation);
            final var dragCoefficient = rigidBody.getDragCoefficient();
            blockPos.set(location.x, location.y, location.z);

            if (!level.getFluidState(blockPos).equals(Fluids.EMPTY.defaultFluidState())) {
                do {
                    blockPos.set(blockPos.above());
                } while (!level.getFluidState(blockPos).equals(Fluids.EMPTY.defaultFluidState()));

                for (var triangle : triangles) {
                    final var centroid = triangle.getCentroid();
                    final var waterHeight = blockPos.getY() - location.y - centroid.y;

                    // check if intersects water
                    if (waterHeight > 0) {
                        final var area = triangle.getArea();

                        final var waterPressure = -1.0f * gravity.y * WATER_DENSITY * waterHeight;
                        final var buoyantForce = v4.set(area).multLocal(-1.0f * waterPressure);

                        final var tangentialVelocity = v5.set(angularVelocity).cross(centroid);
                        final var netVelocity = v6.set(tangentialVelocity).add(linearVelocity);
                        final var dragForce = v7.set(area).multLocal(-0.5f * dragCoefficient * WATER_DENSITY * netVelocity.lengthSquared());
                        dragForce.multLocal(-1.0f * Math.signum(netVelocity.dot(dragForce)));

                        final var netForce = v8.set(buoyantForce).add(dragForce);

                        if (Float.isFinite(netForce.lengthSquared()) && netForce.lengthSquared() > 0) {
                            rigidBody.applyForce(netForce, centroid);
                        }
                    }
                }
            }
        }
    }
}

// TODO server physics doesn't stop on time.
// TODO Fix Tag saving