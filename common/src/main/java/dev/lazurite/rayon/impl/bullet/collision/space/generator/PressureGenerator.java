package dev.lazurite.rayon.impl.bullet.collision.space.generator;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.Triangle;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TODO This class is messy af.
 */
public class PressureGenerator {
    public static final float STOPPING_TIME = 2; // number of time steps to apply slamming force on fluid collision

    // Fluid-Related Constants
    public static final float WATER_DENSITY = 1000f;       // kg/m^3

    // Gas-Related Constants
    public static final float AIR_DENSITY = 1.225f;           // kg/m^3
    public static final float GAS_CONSTANT = 8.3144598f;      // J/(mol·K)
    public static final float MOLAR_MASS_OF_AIR = 0.0289644f; // kg/mol
    public static final float SEA_LEVEL_PRESSURE = 101_325f;  // Pa
    public static final float TEMPERATURE = 300;              // K
    public static final int SEA_LEVEL = 62;                   // m

    public static void step(MinecraftSpace space) {
        final var chunkCache = space.getChunkCache();
        final var timeStep = space.getAccuracy();
        final var gravity = space.getGravity(new Vector3f());

        final var location = new Vector3f();
        final var linearVelocity = new Vector3f();
        final var angularVelocity = new Vector3f();
        final var rotation = new Quaternion();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (!rigidBody.isActive() || (rigidBody.getBuoyancyType() == ElementRigidBody.BuoyancyType.NONE && rigidBody.getDragType() == ElementRigidBody.DragType.NONE)) {
                rigidBody.getSleepTimer().reset();
                continue;
            }

            rigidBody.getPhysicsLocation(location);
            rigidBody.getLinearVelocity(linearVelocity);
            rigidBody.getAngularVelocity(angularVelocity);
            rigidBody.getPhysicsRotation(rotation);

            if (linearVelocity.length() < rigidBody.getLinearSleepingThreshold() && angularVelocity.length() < rigidBody.getAngularSleepingThreshold()) {
                if (rigidBody.getSleepTimer().get() > ElementRigidBody.SLEEP_TIME_IN_SECONDS) {
                    rigidBody.setDeactivationTime(2.0f);
                    continue;
                }
            } else {
                rigidBody.getSleepTimer().reset();
            }

            final var mass = rigidBody.getMass();
            final var density = mass / rigidBody.getMinecraftShape().getVolume();
            final var dragCoefficient = rigidBody.getDragCoefficient();

            final var triangles = rigidBody.getMinecraftShape().getTriangles(rotation);
            final var crossSectionalAreas = new HashMap<Triangle, Float>();
            final var submergedTriangles = new ArrayList<Triangle>();
            float totalCrossSectionalArea = 0.0f;

            for (var triangle : triangles) {
                final var centroid = triangle.getCentroid();
                final var area = triangle.getArea();
                final var tangentialVelocity = new Vector3f(angularVelocity).cross(centroid); // angular velocity converted to linear parallel to edge of circle (tangential)
                final var netVelocity = new Vector3f(tangentialVelocity).addLocal(linearVelocity); // total linear + tangential velocity

                if (Math.signum(netVelocity.dot(area)) == 1) {
                    final var crossSectionalArea = netVelocity.normalize().dot(area);
                    crossSectionalAreas.put(triangle, crossSectionalArea);
                    totalCrossSectionalArea += crossSectionalArea;
                }

                final var blockPos = new BlockPos(
                        location.x + centroid.x,
                        location.y + centroid.y,
                        location.z + centroid.z);

                final var posRelativeToBlockCenter = new Vector3f(centroid).add(location).subtract(Convert.toBullet(blockPos));

                chunkCache.getFluidColumn(blockPos).ifPresent(fluidColumn -> {
                    final var waterHeight = fluidColumn.getTop().blockPos().getY() + fluidColumn.getTopHeight(posRelativeToBlockCenter) - location.y - centroid.y;

                    if (waterHeight > 0.0f) {
                        submergedTriangles.add(triangle);
                    }
                });
            }

            final var totalArea = totalCrossSectionalArea;
            final var addedMassAdjustment = density < 50 ? getAddedMassForceAdjustment(submergedTriangles, mass) : 1.0f;

            for (var triangle : triangles) {
                final var centroid = triangle.getCentroid();
                final var area = triangle.getArea();

                final var blockPos = new BlockPos(
                        location.x + centroid.x,
                        location.y + centroid.y,
                        location.z + centroid.z);

                if (submergedTriangles.contains(triangle)) {
                    final var posRelativeToBlockCenter = new Vector3f(centroid).add(location).subtract(Convert.toBullet(blockPos));

                    final var waterHeight = chunkCache.getFluidColumn(blockPos)
                            .map(fluidColumn -> (float) fluidColumn.getTop().blockPos().getY() + fluidColumn.getTopHeight(posRelativeToBlockCenter) - location.y - centroid.y).orElse(0.0f);

                    chunkCache.getFluidColumn(new BlockPos(location.x, location.y, location.z)).ifPresent(fluidColumn -> {
                        final var flowForce = new Vector3f(fluidColumn.getFlow());

                        if (Float.isFinite(flowForce.lengthSquared()) && flowForce.lengthSquared() > 0.0f) {
                            rigidBody.applyForce(flowForce, centroid);
                        }
                    });

                    /* Do water buoyancy */
                    if (rigidBody.isWaterBuoyancyEnabled()) {
                        /* Check to make sure the triangle centroid is actually submerged */
                        final var pressure = gravity.y * WATER_DENSITY * waterHeight;
                        final var buoyantForce = new Vector3f(area).multLocal(pressure);

                        if (Float.isFinite(buoyantForce.lengthSquared()) && buoyantForce.lengthSquared() > 0.0f) {
                            rigidBody.applyForce(buoyantForce.multLocal(addedMassAdjustment), centroid);
                        }
                    }

                    /* Do water drag */
                    if (rigidBody.isWaterDragEnabled()) {
                        final var tangentialVelocity = new Vector3f(angularVelocity).cross(centroid); // angular velocity converted to linear parallel to edge of circle (tangential)
                        final var netVelocity = new Vector3f(tangentialVelocity).addLocal(linearVelocity); // total linear + tangential velocity

                        if (Math.signum(netVelocity.dot(area)) == 1) {
                            final var dragForce = new Vector3f(area).multLocal(-0.5f * dragCoefficient * WATER_DENSITY * netVelocity.lengthSquared());
                            dragForce.multLocal(-1.0f * Math.signum(netVelocity.dot(dragForce)));

                            /* This stopping force is how we prevent objects from entering orbit upon touching water :( */
                            final var stoppingForce = new Vector3f(netVelocity).multLocal(-1.0f * rigidBody.getMass() * crossSectionalAreas.get(triangle) / totalArea).divideLocal(timeStep);

                            /* So if the stopping force is smaller, we apply that instead. */
                            if (dragForce.length() < stoppingForce.length()) {
                                rigidBody.applyForce(dragForce.multLocal(addedMassAdjustment), centroid);
                            } else {
                                rigidBody.applyForce(stoppingForce.divideLocal(STOPPING_TIME), centroid);
                            }
                        }
                    }
                } else {
                    // TODO this is rly borky
//                            if (rigidBody.isAirBuoyancyEnabled()) {
//                                final var pressure = (float) (SEA_LEVEL_PRESSURE * Math.exp(MOLAR_MASS_OF_AIR * gravity.y * (SEA_LEVEL - location.y - centroid.y) / (GAS_CONSTANT * TEMPERATURE)));
//                                final var buoyantForce = new Vector3f(area).multLocal(pressure);
//
//                                if (Float.isFinite(buoyantForce.lengthSquared()) && buoyantForce.lengthSquared() > 0.0f) {
//                                    rigidBody.applyForce(buoyantForce, centroid);
//                                }
//                            }

                    /* Do (complex) air drag */
                    if (rigidBody.isAirDragEnabled()) {
                        /* air_density_at_sea_level * e^(gravity * molar_mass_of_air * sea_level / (gas_constant * temperature)) */
                        /* 1.2 * e^(-9.8 * 0.0289644 * 62 / (8.3144598 * 300) */
                        final var airDensity = (float) (AIR_DENSITY * Math.exp(MOLAR_MASS_OF_AIR * gravity.y * (SEA_LEVEL - location.y - centroid.y) / (GAS_CONSTANT * TEMPERATURE)));

                        final var tangentialVelocity = new Vector3f(angularVelocity).cross(centroid); // angular velocity converted to linear parallel to edge of circle (tangential)
                        final var netVelocity = new Vector3f(tangentialVelocity).addLocal(linearVelocity); // total linear + tangential velocity

                        if (Math.signum(netVelocity.dot(area)) == 1) {
                            final var dragForce = new Vector3f(area).multLocal(-0.5f * dragCoefficient * airDensity * netVelocity.lengthSquared());
                            dragForce.multLocal(-1.0f * Math.signum(netVelocity.dot(dragForce))); // make sure all the vectors are facing the same way

                            if (Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0.0f) {
                                rigidBody.applyForce(dragForce, centroid);
                            }
                        }
                    }
                }
            }

            /* Do (simple) air drag */
            if (rigidBody.getDragType() == ElementRigidBody.DragType.SIMPLE) {
                final var box = rigidBody.getCollisionShape().boundingBox(new Vector3f(), new Quaternion(), new BoundingBox());
                final var area = box.getExtent(new Vector3f()).lengthSquared();
                final var dragForce = new Vector3f(linearVelocity.normalize()).multLocal(-0.5f * area * dragCoefficient * AIR_DENSITY * linearVelocity.lengthSquared());

                if (Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0.0f) {
                    rigidBody.applyCentralForce(dragForce);
                }
            }
        }
    }

    public static float getAddedMassForceAdjustment(List<Triangle> triangles, float mass) {
        final var n = triangles.size();
        final var sum = triangles.stream().mapToDouble(triangle -> triangle.getArea().length() * triangle.getCentroid().length()).sum();
        final var addedMass = WATER_DENSITY / (6 * n) * sum;
        return (float) (mass / (mass + addedMass));
    }
}