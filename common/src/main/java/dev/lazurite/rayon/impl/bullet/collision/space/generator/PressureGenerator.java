package dev.lazurite.rayon.impl.bullet.collision.space.generator;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.Fluids;

public class PressureGenerator {
    public static final float AIR_DENSITY = 1.2f;
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
    private static final Vector3f v9 = new Vector3f();
    private static final Vector3f v10 = new Vector3f();
    private static final Quaternion q1 = new Quaternion();

    public static void step(MinecraftSpace space) {
        final var gravity = space.getGravity(null);
        final var level = space.getLevel();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            // No point doing all this if buoyancy and drag are disabled...
            if (!rigidBody.buoyantForcesEnabled() && !rigidBody.dragForcesEnabled()) {
                continue;
            }

            final var location = rigidBody.getPhysicsLocation(v1);
            final var linearVelocity = rigidBody.getLinearVelocity(v2);
            final var angularVelocity = rigidBody.getAngularVelocity(v3);
            final var rotation = rigidBody.getPhysicsRotation(q1);
            final var triangles = rigidBody.getCollisionShape().getTriangles(rotation);
            final var dragCoefficient = rigidBody.getDragCoefficient();
            blockPos.set(location.x, location.y, location.z);
            boolean isUnderwater = false;

            while (!level.getFluidState(blockPos).equals(Fluids.EMPTY.defaultFluidState())) {
                blockPos.set(blockPos.above());
                isUnderwater = true;
            }

            for (var triangle : triangles) {
                final var centroid = triangle.getCentroid();
                final var area = triangle.getArea();
                final var waterHeight = blockPos.getY() - location.y - centroid.y;
                final var density = waterHeight > 0 ? WATER_DENSITY : AIR_DENSITY;

                if (rigidBody.buoyantForcesEnabled() && isUnderwater) {
                    final var pressure = gravity.y * density * (waterHeight > 0 ? waterHeight : centroid.y);
                    final var buoyantForce = v4.set(area).multLocal(pressure);

                    if (Float.isFinite(buoyantForce.lengthSquared()) && buoyantForce.lengthSquared() > 0.0f) {
                        rigidBody.applyForce(buoyantForce, centroid);
                    }
                }

                if (rigidBody.dragForcesEnabled() && (isUnderwater || rigidBody.getDragType() == ElementRigidBody.DragType.REALISTIC)) {
                    final var tangentialVelocity = v5.set(angularVelocity).cross(centroid);
                    final var netVelocity = v6.set(tangentialVelocity).addLocal(linearVelocity);

                    final var dragForce = v7.set(area).multLocal(-0.5f * dragCoefficient * density * netVelocity.lengthSquared());
                    dragForce.multLocal(-1.0f * Math.signum(netVelocity.dot(dragForce)));

                    if (Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0.0f) {
                        rigidBody.applyForce(dragForce, centroid);
                    }
                }
            }

            if (!isUnderwater && rigidBody.dragForcesEnabled() && rigidBody.getDragType() == ElementRigidBody.DragType.SIMPLE) {
                final var box = rigidBody.getCollisionShape().boundingBox(v8, q1, new BoundingBox());
                final var area = (float) Math.pow(box.getExtent(v9).length(), 2);
                final var dragForce = v10.set(linearVelocity.normalize()).multLocal(-0.5f * area * dragCoefficient * AIR_DENSITY * linearVelocity.lengthSquared());

                if (Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0.0f) {
                    rigidBody.applyCentralForce(dragForce);
                }
            }
        }
    }
}