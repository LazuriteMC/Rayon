package dev.lazurite.rayon.impl.bullet.collision.space.generator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.Fluids;

public class PressureGenerator {
    public static final float WATER_DENSITY = 1000f;
    public static final float AIR_DENSITY = 1.2f;

    public static final float GAS_CONSTANT = 8.3144598f; // J/(mol·K)
    public static final float MOLAR_MASS_OF_AIR = 0.0289644f; // kg/mol
    public static final float SEA_LEVEL_PRESSURE = 101_325f; // Pa
    public static final float TEMPERATURE = 300; // K
    public static final int SEA_LEVEL = 62; // m

    private static final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
    private static final Vector3f v1 = new Vector3f();
    private static final Vector3f v2 = new Vector3f();
    private static final Vector3f v3 = new Vector3f();
    private static final Vector3f v4 = new Vector3f();
    private static final Vector3f v5 = new Vector3f();
    private static final Vector3f v6 = new Vector3f();
    private static final Vector3f v7 = new Vector3f();
    private static final Vector3f v8 = new Vector3f();
    private static final Vector3f v9 = new Vector3f();
    private static final Vector3f v10 = new Vector3f();
    private static final Quaternion q1 = new Quaternion();

    public static void step(MinecraftSpace space) {
        final var gravity = space.getGravity(null);
        final var level = space.getLevel();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (rigidBody.isStatic() || !rigidBody.isActive() || (!rigidBody.buoyantForcesEnabled() && !rigidBody.dragForcesEnabled())) {
                continue;
            }

            final var location = rigidBody.getPhysicsLocation(v1);
            final var linearVelocity = rigidBody.getLinearVelocity(v2);
            final var angularVelocity = rigidBody.getAngularVelocity(v3);
            final var rotation = rigidBody.getPhysicsRotation(q1);
            final var triangles = rigidBody.getCollisionShape().getTriangles(rotation);
            final var dragCoefficient = rigidBody.getDragCoefficient();

            blockPos.set(location.x, location.y, location.z);

            while (!level.getFluidState(blockPos).equals(Fluids.EMPTY.defaultFluidState())) {
                blockPos.set(blockPos.above());
            }

            for (var triangle : triangles) {
                final var centroid = triangle.getCentroid();
                final var area = triangle.getArea();

                boolean isUnderwater = !level.getFluidState(new BlockPos(location.x + centroid.x, location.y + centroid.y, location.z + centroid.z)).equals(Fluids.EMPTY.defaultFluidState());

                if (rigidBody.buoyantForcesEnabled()) {
                    final var pressure = (float) (isUnderwater ? gravity.y * WATER_DENSITY * (blockPos.getY() - location.y - centroid.y):
                            SEA_LEVEL_PRESSURE * Math.exp(MOLAR_MASS_OF_AIR * gravity.y *
                                    (SEA_LEVEL - location.y - centroid.y) / (GAS_CONSTANT * TEMPERATURE)));
                    final var buoyantForce = v4.set(area).multLocal(pressure);

                    if (Float.isFinite(buoyantForce.lengthSquared()) && buoyantForce.lengthSquared() > 0.0f) {
                        rigidBody.applyForce(buoyantForce, centroid);
                    }
                }

                if (rigidBody.dragForcesEnabled()) {
                    final var tangentialVelocity = v5.set(angularVelocity).cross(centroid);
                    final var netVelocity = v6.set(tangentialVelocity).addLocal(linearVelocity);
                    final var density = (float) (isUnderwater ? WATER_DENSITY : AIR_DENSITY * Math.exp(gravity.y * MOLAR_MASS_OF_AIR * (SEA_LEVEL - location.y - centroid.y) / (GAS_CONSTANT * TEMPERATURE)));

                    final var dragForce = v7.set(area).multLocal(-0.5f * dragCoefficient * density * netVelocity.lengthSquared());
                    dragForce.multLocal(-1.0f * Math.signum(netVelocity.dot(dragForce)));

                    if (Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0.0f) {
                        rigidBody.applyForce(dragForce, centroid);
                    }
                }
            }
//            } else if (rigidBody.dragForcesEnabled()) {
//                final var box = rigidBody.getCollisionShape().boundingBox(v8, q1, new BoundingBox());
//                final var area = box.getExtent(v9).lengthSquared();
//                final var dragForce = v10.set(linearVelocity.normalize()).multLocal(-0.5f * area * dragCoefficient * AIR_DENSITY * linearVelocity.lengthSquared());
//
//                if (Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0.0f) {
//                    rigidBody.applyCentralForce(dragForce);
//                }
//            }
        }
    }
}