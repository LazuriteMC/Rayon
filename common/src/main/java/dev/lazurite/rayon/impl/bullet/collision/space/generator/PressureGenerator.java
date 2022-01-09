package dev.lazurite.rayon.impl.bullet.collision.space.generator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.Triangle;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.space.cache.ChunkCache;
import net.minecraft.core.BlockPos;

import java.util.List;

public class PressureGenerator {
    public static final float WATER_DENSITY = 1000f;
    public static final float AIR_DENSITY = 1.2f;

    public static final float GAS_CONSTANT = 8.3144598f; // J/(mol·K)
    public static final float MOLAR_MASS_OF_AIR = 0.0289644f; // kg/mol
    public static final float SEA_LEVEL_PRESSURE = 101_325f; // Pa
    public static final float TEMPERATURE = 300; // K
    public static final int SEA_LEVEL = 62; // m

    public static final float WATER_BLOCK_OFFSET = 0.125f;

    public static float getAddedMassForceAdjustment(List<Triangle> triangles, float mass) {
        final var n = triangles.size();
        final var sum = triangles.stream().mapToDouble(triangle -> triangle.getArea().length() * triangle.getCentroid().length()).sum();
        final var addedMass = WATER_DENSITY / (6 * n) * sum;
        return (float) (mass / (mass + addedMass));
    }

    public static float getWaterSurfaceHeight(ChunkCache chunkCache, Vector3f location) {
        return chunkCache.getFluidColumn(new BlockPos(location.x, location.y, location.z))
                .map(fluidColumn -> (float) fluidColumn.getTop().blockPos().getY()).orElse(0.0f);
    }

    public static void step(MinecraftSpace space) {
        final var gravity = space.getGravity(null);
        final var chunkCache = space.getChunkCache();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (!rigidBody.isActive() || (rigidBody.getBuoyancyType() == ElementRigidBody.BuoyancyType.NONE && rigidBody.getDragType() == ElementRigidBody.DragType.NONE)) {
                continue;
            }

            final var mass = rigidBody.getMass();
            final var volume = rigidBody.getCollisionShape().aabbVolume();
            final var rigidBodyDensity = mass / volume;

            final var location = rigidBody.getPhysicsLocation(new Vector3f());
            final var linearVelocity = rigidBody.getLinearVelocity(new Vector3f());
            final var angularVelocity = rigidBody.getAngularVelocity(new Vector3f());
            final var rotation = rigidBody.getPhysicsRotation(new Quaternion());
            final var momentum = new Vector3f(linearVelocity).multLocal(mass);
            final var triangles = rigidBody.getCollisionShape().getTriangles(rotation);
            final var dragCoefficient = rigidBody.getDragCoefficient();

            final var waterHeight = getWaterSurfaceHeight(chunkCache, location);

            final var submergedTriangles = triangles.stream().filter(triangle -> chunkCache.getFluidColumn(new BlockPos(
                    location.x + triangle.getCentroid().x,
                    location.y + triangle.getCentroid().y,
                    location.z + triangle.getCentroid().z
            )).isPresent()).toList();

            final var forceAdjustment = getAddedMassForceAdjustment(submergedTriangles, mass);

            for (var triangle : submergedTriangles) {
                final var centroid = triangle.getCentroid();
                final var area = triangle.getArea();
                final var waterHeightOffset = waterHeight - location.y - centroid.y - WATER_BLOCK_OFFSET;

                if (waterHeightOffset > 0.0f) {
                    if (rigidBody.isWaterBuoyancyEnabled()) {
                        final var pressure = gravity.y * WATER_DENSITY * waterHeightOffset;
                        final var buoyantForce = new Vector3f(area).multLocal(pressure); // area * pressure = buoyant force

                        if (Float.isFinite(buoyantForce.lengthSquared()) && buoyantForce.lengthSquared() > 0.0f) {
                            rigidBody.applyForce(buoyantForce.multLocal(forceAdjustment), centroid);
                        }
                    }

                    if (rigidBody.isWaterDragEnabled()) {
                        /* FUDGE ZONE */
                        // monka math right here
                        if (linearVelocity.length() > 2 && rigidBodyDensity < 100) {
                            final var time = 1.0f;
                            final var stopForce = new Vector3f(0.0f, -1.0f * linearVelocity.y * mass / time, 0.0f);
                            rigidBody.applyForce(stopForce, centroid);
                        } else {
                            final var tangentialVelocity = new Vector3f(angularVelocity).cross(centroid); // angular velocity converted to linear parallel to edge of circle (tangential)
                            final var netVelocity = new Vector3f(tangentialVelocity).addLocal(linearVelocity); // total linear + tangential velocity

                            final var dragForce = new Vector3f(area).multLocal(-0.5f * dragCoefficient * WATER_DENSITY * netVelocity.lengthSquared());
                            dragForce.multLocal(-1.0f * Math.signum(netVelocity.dot(dragForce)));

                            if (Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0.0f) {
                                rigidBody.applyForce(dragForce.multLocal(forceAdjustment), centroid);
                            }
                        }
                    }
                }
            }

            final var theOtherTriangles = triangles.stream().filter(submergedTriangles::contains).toList();

            for (var triangle : theOtherTriangles) {
                final var centroid = triangle.getCentroid();
                final var area = triangle.getArea();
                final var netForce = new Vector3f();

                if (rigidBody.isAirBuoyancyEnabled()) {
                    final var pressure = (float) (SEA_LEVEL_PRESSURE * Math.exp(MOLAR_MASS_OF_AIR * gravity.y * (SEA_LEVEL - location.y - centroid.y) / (GAS_CONSTANT * TEMPERATURE)));
                    netForce.addLocal(new Vector3f(area).multLocal(pressure)); // area * pressure = buoyant force
                }

                if (rigidBody.isAirDragEnabled()) {
                    /* air_density_at_sea_level * e^(gravity * molar_mass_of_air * sea_level / (gas_constant * temperature)) */
                    /* 1.2 * e^(-9.8 * 0.0289644 * 62 / (8.3144598 * 300) */
                    final var density = (float) (AIR_DENSITY * Math.exp(MOLAR_MASS_OF_AIR * gravity.y * (SEA_LEVEL - location.y - centroid.y) / (GAS_CONSTANT * TEMPERATURE)));

                    final var tangentialVelocity = new Vector3f(angularVelocity).cross(centroid); // angular velocity converted to linear parallel to edge of circle (tangential)
                    final var netVelocity = new Vector3f(tangentialVelocity).addLocal(linearVelocity); // total linear + tangential velocity

                    final var dragForce = new Vector3f(area).multLocal(-0.5f * dragCoefficient * density * netVelocity.lengthSquared());
                    dragForce.multLocal(-1.0f * Math.signum(netVelocity.dot(dragForce))); // make sure all the vectors are facing the same way
                    netForce.addLocal(dragForce);
                }

                if (Float.isFinite(netForce.lengthSquared()) && netForce.lengthSquared() > 0.0f) {
                    rigidBody.applyForce(netForce, centroid);
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