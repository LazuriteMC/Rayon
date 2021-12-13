package dev.lazurite.rayon.impl.bullet.collision.space.generator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class PressureGenerator {
    public static void step(MinecraftSpace space) {
        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            final var rigidBodyPos = rigidBody.getPhysicsLocation(null);
            final var rigidBodyRot = Convert.toMinecraft(rigidBody.getPhysicsRotation(null));
            final var rigidBodyShape = rigidBody.getCollisionShape();
            final var triangles = rigidBodyShape.getTriangles();

            final var gravity = -1.0f * space.getGravity(null).y;
            final var waterDensity = 1000f; //1.5f * (float) (rigidBody.getMass() / (rigidBodyBox.getXsize() * rigidBodyBox.getYsize() * rigidBodyBox.getZsize()));
            final var level = space.getLevel();

            var blockPos = new BlockPos(rigidBodyPos.x, rigidBodyPos.y, rigidBodyPos.z);
            FluidState state = level.getFluidState(blockPos);

            var areaSum = 0.0f;

            if (!state.equals(Fluids.EMPTY.defaultFluidState())) {
                do {
                    blockPos = blockPos.above();
                    state = level.getFluidState(blockPos);
                } while (!state.equals(Fluids.EMPTY.defaultFluidState()));

                final var linearVelocity = rigidBody.getLinearVelocity(null);
                final var angularVelocity = rigidBody.getAngularVelocity(null);
                final var dragCoefficient = rigidBody.getDragCoefficient();

                for (int i = 0; i < triangles.size(); i += 3) {
                    var v1 = Convert.toMinecraft(triangles.get(i));
                    var v2 = Convert.toMinecraft(triangles.get(i + 1));
                    var v3 = Convert.toMinecraft(triangles.get(i + 2));

                    v1.transform(rigidBodyRot);
                    v2.transform(rigidBodyRot);
                    v3.transform(rigidBodyRot);

                    var p1 = Convert.toBullet(v1);
                    var p2 = Convert.toBullet(v2);
                    var p3 = Convert.toBullet(v3);

                    final var croid = new Vector3f(
                            (p1.x + p2.x + p3.x) / 3.0f,
                            (p1.y + p2.y + p3.y) / 3.0f,
                            (p1.z + p2.z + p3.z) / 3.0f);

                    final var waterHeight = blockPos.getY() - rigidBodyPos.y - croid.y;

                    final var e1 = p1.subtract(p2);
                    final var e2 = p2.subtract(p3);
                    final var area = e2.cross(e1).multLocal(0.5f);
                    area.multLocal(Math.signum(croid.dot(area))); // make sure it faces outward

                    // check if intersects water
                    if (waterHeight > 0) {
                        {
                            final var waterPressure = gravity * waterDensity * waterHeight;
                            final var buoyantForce = new Vector3f(area).multLocal(-1.0f * waterPressure);

                            // make sure the face is in the direction of motion
                            // for use in linear drag force later
                            if (linearVelocity.dot(area) > 0) {
                                areaSum += area.dot(linearVelocity.normalize());
                            }

                            if (Float.isFinite(buoyantForce.lengthSquared()) && buoyantForce.lengthSquared() > 0) {
                                rigidBody.applyForce(buoyantForce, croid);
                            }
                        }

                        {
                            final var tangentialVelocity = new Vector3f(angularVelocity).cross(croid);
//                            final var crossSectionalArea = area.dot(tangentialVelocity.normalize());
                            final var dragForce = area.multLocal(-0.5f * dragCoefficient * waterDensity * tangentialVelocity.lengthSquared());
                            dragForce.multLocal(-1.0f * Math.signum(tangentialVelocity.dot(dragForce)));


                            if (Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0) {
                                rigidBody.applyForce(dragForce, croid);
                            }
                        }
                    }
                }

                final var dragForce = linearVelocity.normalize().multLocal(-0.5f * areaSum * dragCoefficient * waterDensity * linearVelocity.lengthSquared());

                if (Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0) {
                    rigidBody.applyCentralForce(dragForce);
                }
            }

//            final var fluidObjects = rigidBody.getTerrainObjects().values().stream()
//                    .filter(terrainObject -> terrainObject.getCollisionObject() instanceof Terrain.Fluid).toList();

//                    .map(terrainObject -> (Terrain.Fluid) terrainObject.getCollisionObject())
//                    .filter(fluid -> Convert.toMinecraft(fluid.boundingBox(new BoundingBox())).intersects(rigidBodyBox2))
//                    .map(fluid -> fluid.boundingBox(new BoundingBox()).getMax(new Vector3f()).y)
//                    .mapToDouble(height -> (double) height).average().getAsDouble();

//            float height = 0.0f;
//            float volume = 0.0f;
//            for (var fluidObject : fluidObjects) {
//                height += fluidObject.getCollisionObject().getPhysicsLocation(null).y;
//                final var box = Convert.toMinecraft(fluidObject.getCollisionObject().boundingBox(new BoundingBox()));
//                if (box.intersects(rigidBodyBox)) {
//                    final var intersection = box.intersect(rigidBodyBox);
//                    volume += intersection.getXsize() * intersection.getYsize() * intersection.getZsize();
//                }
//            }
//            float averageWaterHeight = height / fluidObjects.size() + 0.5f;
//            boolean hit = false;


//            for (int i = 0; i < accuracy; i++) {
//                            final var fluidPos = Convert.toBullet(f);//                for (int j = 0; j < accuracy; j++) {
//                    for (int k = 0; k < accuracy; k++) {
//                        final var rBox = new AABB(
//                                rigidBodyBox.minX + rigidBodyBox.getXsize() * (i / (float) accuracy),
//                                rigidBodyBox.minY + rigidBodyBox.getXsize() * (j / (float) accuracy),
//                                rigidBodyBox.minZ + rigidBodyBox.getZsize() * (k / (float) accuracy),
//                                rigidBodyBox.minX + rigidBodyBox.getXsize() * ((i + 1) / (float) accuracy),
//                                rigidBodyBox.minY + rigidBodyBox.getYsize() * ((j + 1) / (float) accuracy),
//                                rigidBodyBox.minZ + rigidBodyBox.getZsize() * ((k + 1) / (float) accuracy));
//
//                        final var rPos = Convert.toBullet(rBox.getCenter());
//
//                        for (var fluidObject : fluidObjects) {
//                            final var fluidBox = Convert.toMinecraft(fluidObject.getCollisionObject().boundingBox(null));
//
//                            for (int z = 0; z < accuracy; z++) {
//                                for (int x = 0; x < accuracy; x++) {
//                                    final var smallBox = new AABB(
//                                            fluidBox.minX + fluidBox.getXsize() * (x / (float) accuracy),
//                                            fluidBox.minY,
//                                            fluidBox.minZ + fluidBox.getZsize() * (z / (float) accuracy),
//                                            fluidBox.minX + fluidBox.getXsize() * ((x + 1) / (float) accuracy),
//                                            fluidBox.maxY,
//                                            fluidBox.minZ + fluidBox.getZsize() * ((z + 1) / (float) accuracy));
//
//            final var accuracy = 2.0f;
//
//             TODO server physics doesn't stop on time.
            // TODO Fix Tag saving
//
//            for (int z = 0; z < accuracy; z++) {
//                for (int y = 0; y < accuracy; y++) {
//                    for (int x = 0; x < accuracy; x++) {
//                        final var smallBox = new AABB(
//                                rigidBodyBox.minX + rigidBodyBox.getXsize() * (x / accuracy),
//                                rigidBodyBox.minY + rigidBodyBox.getXsize() * (y / accuracy),
//                                rigidBodyBox.minZ + rigidBodyBox.getZsize() * (z / accuracy),
//                                rigidBodyBox.minX + rigidBodyBox.getXsize() * ((x + 1) / accuracy),
//                                rigidBodyBox.minY + rigidBodyBox.getYsize() * ((y + 1) / accuracy),
//                                rigidBodyBox.minZ + rigidBodyBox.getZsize() * ((z + 1) / accuracy));
//
//                        var smallPos = VectorHelper.toVector3f(smallBox.getCenter());
//                        smallPos.sub(Convert.toMinecraft(rigidBodyPos));
//                        smallPos.transform(Convert.toMinecraft(rigidBodyRot));

//                        if (rigidBodyBox.minY < averageWaterHeight) {
//                        if (volume > 0) {
//                            System.out.println(averageWaterHeight + ", " + rigidBodyBox.maxY);

//                            rigidBodyBox = new AABB(
//                                    rigidBodyBox.minX, rigidBodyBox.minY, rigidBodyBox.minZ,
//                                    rigidBodyBox.maxX, Math.min(averageWaterHeight, rigidBodyBox.maxY), rigidBodyBox.maxZ);

//                            final var volume = rigidBodyBox.getXsize() * rigidBodyBox.getYsize() * rigidBodyBox.getZsize();
//                            System.out.println("volume: " + volume);
//
//
//                            final var points = new com.mojang.math.Vector3f[8];
//                            points[0] = new com.mojang.math.Vector3f((float) rigidBodyBox.minX, (float) rigidBodyBox.minY, (float) rigidBodyBox.minZ);
//                            points[1] = new com.mojang.math.Vector3f((float) rigidBodyBox.minX, (float) rigidBodyBox.minY, (float) rigidBodyBox.maxZ);
//                            points[2] = new com.mojang.math.Vector3f((float) rigidBodyBox.maxX, (float) rigidBodyBox.minY, (float) rigidBodyBox.minZ);
//                            points[3] = new com.mojang.math.Vector3f((float) rigidBodyBox.maxX, (float) rigidBodyBox.minY, (float) rigidBodyBox.maxZ);
//                            points[4] = new com.mojang.math.Vector3f((float) rigidBodyBox.minX, (float) rigidBodyBox.maxY, (float) rigidBodyBox.minZ);
//                            points[5] = new com.mojang.math.Vector3f((float) rigidBodyBox.minX, (float) rigidBodyBox.maxY, (float) rigidBodyBox.maxZ);
//                            points[6] = new com.mojang.math.Vector3f((float) rigidBodyBox.maxX, (float) rigidBodyBox.maxY, (float) rigidBodyBox.minZ);
//                            points[7] = new com.mojang.math.Vector3f((float) rigidBodyBox.maxX, (float) rigidBodyBox.maxY, (float) rigidBodyBox.maxZ);
//
//                            final var rot = Convert.toMinecraft(rigidBodyRot);
//                            com.mojang.math.Vector3f lowestPoint = null;
//
//                            for (var point : points) {
//                                point.sub(Convert.toMinecraft(rigidBodyPos));
//                                point.transform(rot);
//
//                                if (lowestPoint == null) {
//                                    lowestPoint = point;
//                                } else if (point.y() < lowestPoint.y()) {
//                                    lowestPoint = point;
//                                }
//                            }
//
//                            lowestPoint.set(lowestPoint.x(), (averageWaterHeight + lowestPoint.y()) * 0.5f, lowestPoint.z());
//
//                        for (var fluid : fluidObjects) {
//                            final var fluidBox = Convert.toMinecraft(fluid.getCollisionObject().boundingBox(new BoundingBox()));
//                            var fluidPos = fluid.getCollisionObject().getPhysicsLocation(null);
//                            fluidPos = fluidPos.subtract(rigidBodyPos);

//                            final var f = VectorHelper.toVector3f(VectorHelper.toVec3(Convert.toMinecraft(rigidBodyPos.subtract(fluidPos))));
//                            f.transform(Convert.toMinecraft(rigidBodyRot));
//
//                            rigidBodyPos = Convert.toBullet(f);
//                            rigidBodyPos.add(fluidPos);
//                            if (smallBox.intersects(fluidBox)) {
//                                      rigidBodyPos = Convert.toBullet(f);      final var fluidPos = Convert.toBullet(smallBox.getCenter());

//                            final var collisionPos = Convert.toBullet(smallPos).add(fluidPos).multLocal(0.5f);

//                                final var maxVolume = smallBox.getXsize() * smallBox.getYsize() * smallBox.getZsize();
//                                final var intersection = smallBox.intersect(fluidBox);
//                                final var volume = intersection.getXsize() * intersection.getYsize() * intersection.getZsize();
//                            final var volume = calculateAABBOverlap(
//                                    fluidPos.x - smallPos.x(),
//                                    fluidPos.y - smallPos.y(),
//                                    fluidPos.z - smallPos.z());

//                            if (volume > 0) {
//                                hit = true;

//                                final var maxVolume = 2.0f;
//                                final var force = new Vector3f(0, (float) (gravity * waterDensity * Math.min(volume, maxVolume)), 0);
//                                final var collisionPos = Convert.toBullet(lowestPoint);
////
//                                if (Float.isFinite(force.lengthSquared()) && force.lengthSquared() > 0.0f && rigidBody.buoyantForcesEnabled()) {
//                                    rigidBody.applyForce(force, collisionPos);
//                                    applyDragForce(rigidBody, collisionPos, waterDensity);
//                                    rigidBody.applyCentralForce(force);
//                                    rigidBody.applyTorque(collisionPos.cross(force).multLocal(-1));
//                                    rigidBody.applyTorque(collisionPos.cross(force));
//                                }
//                            }
//                        }
//                            }
//                        }
//                    }
//                }
//            }

//                                }
//                            }
//                        }
//                    }
//                }
//            }

//            if (rigidBody.dragForcesEnabled()) {
//                if (!hit) {
//                    applyDragForce(rigidBody, rigidBodyPos, 1.2f);
//                }
//            }
        }
    }

//    static final float AABB_RADIUS = 0.5f;
//
//    private static double calculateAABBOverlap(double xOffset, double yOffset, double zOffset) {
//        xOffset = Math.abs(xOffset);
//        yOffset = Math.abs(yOffset);
//        zOffset = Math.abs(zOffset);
//        if (xOffset >= AABB_RADIUS * 2 || yOffset >= AABB_RADIUS * 2 || zOffset >= AABB_RADIUS * 2) {
//            return 0;
//        }
//        return (AABB_RADIUS * 2 - xOffset) * (AABB_RADIUS * 2 - yOffset) * (AABB_RADIUS * 2 - zOffset);
//    }











//            final var centerPoints = new ArrayList<Vector3f>();
//
//            for (var terrain : rigidBody.getTerrainObjects().values()) {
//                if (terrain.getCollisionObject() instanceof Terrain.Fluid fluid) {
//                    final var fluidBox = Convert.toMinecraft(terrain.getCollisionObject().boundingBox(null));
//
//                    if (fluidBox.intersects(rigidBodyBox)) {
//                        final var intersection = fluidBox.intersect(rigidBodyBox);
//                        volume += intersection.getXsize() * intersection.getYsize() * intersection.getZsize();
////                        centerPoints.add(Convert.toBullet(intersection.getCenter().subtract(rigidBodyBox.getCenter())));
//
//                    }
//                }
//            }
//            final var accuracy = 2.0f;

//            final var rays = new ArrayList<PhysicsRayTestResult>();
//
//            final var a0 = new Vector3f((float) (rigidBodyBox.minX + rigidBodyBox.getXsize() * 0.5f), (float) rigidBodyBox.minY, (float) rigidBodyBox.minZ);
//            final var a1 = new Vector3f((float) (rigidBodyBox.minX + rigidBodyBox.getXsize() * 0.5f), (float) rigidBodyBox.maxY, (float) rigidBodyBox.minZ);
//
//            final var b0 = new Vector3f((float) (rigidBodyBox.minX + rigidBodyBox.getXsize()), (float) rigidBodyBox.minY, (float) (rigidBodyBox.minZ + rigidBodyBox.getZsize() * 0.5f));
//            final var b1 = new Vector3f((float) (rigidBodyBox.minX + rigidBodyBox.getXsize()), (float) rigidBodyBox.minY, (float) (rigidBodyBox.minZ + rigidBodyBox.getZsize() * 0.5f));
//
//            final var c0 = new Vector3f((float) (rigidBodyBox.minX + rigidBodyBox.getXsize() * 0.5f), (float) rigidBodyBox.minY, (float) (rigidBodyBox.minZ + rigidBodyBox.getZsize()));
//            final var c1 = new Vector3f((float) (rigidBodyBox.minX + rigidBodyBox.getXsize() * 0.5f), (float) rigidBodyBox.minY, (float) (rigidBodyBox.minZ + rigidBodyBox.getZsize()));
//
//            final var d0 = new Vector3f((float) rigidBodyBox.minX, (float) rigidBodyBox.minY, (float) (rigidBodyBox.minZ + rigidBodyBox.getZsize() * 0.5f));
//            final var d1 = new Vector3f((float) rigidBodyBox.minX, (float) rigidBodyBox.minY, (float) (rigidBodyBox.minZ + rigidBodyBox.getZsize() * 0.5f));
//
//            rays.addAll(space.rayTest(a0, a1));
//            rays.addAll(space.rayTest(b0, b1));
//            rays.addAll(space.rayTest(c0, c1));
//            rays.addAll(space.rayTest(d0, d1));
//
//            rays.removeIf(ray -> ray.getCollisionObject() instanceof Terrain.Fluid);
//            float sum = 0.0f;
//
//            for (var ray : rays) {
//                sum += ray.getHitFraction();
//            }
//
//            float average = sum / (float) rays.size();

//                    final var rotation = Convert.toMinecraft(rigidBody.getPhysicsRotation(new Quaternion()));
//                    final var location = Convert.toMinecraft(rigidBody.getPhysicsLocation(new Vector3f()));
//
//                    final var v1 = Convert.toMinecraft(new Vector3f((float) -rigidBodyBox.getXsize() * 0.5f, (float) -rigidBodyBox.getYsize() * 0.5f, (float) -rigidBodyBox.getZsize() * 0.5f));
//                    final var v2 = Convert.toMinecraft(new Vector3f((float) rigidBodyBox.getXsize() * 0.5f, (float) -rigidBodyBox.getYsize() * 0.5f, (float) -rigidBodyBox.getZsize() * 0.5f));
//                    final var v3 = Convert.toMinecraft(new Vector3f((float) rigidBodyBox.getXsize() * 0.5f, (float) -rigidBodyBox.getYsize() * 0.5f, (float) rigidBodyBox.getZsize() * 0.5f));
//                    final var v4 = Convert.toMinecraft(new Vector3f((float) -rigidBodyBox.getXsize() * 0.5f, (float) -rigidBodyBox.getYsize() * 0.5f, (float) rigidBodyBox.getZsize() * 0.5f));
//                    final var v5 = Convert.toMinecraft(new Vector3f((float) -rigidBodyBox.getXsize() * 0.5f, (float) rigidBodyBox.getYsize() * 0.5f, (float) -rigidBodyBox.getZsize() * 0.5f));
//                    final var v6 = Convert.toMinecraft(new Vector3f((float) rigidBodyBox.getXsize() * 0.5f, (float) rigidBodyBox.getYsize() * 0.5f, (float) -rigidBodyBox.getZsize() * 0.5f));
//                    final var v7 = Convert.toMinecraft(new Vector3f((float) rigidBodyBox.getXsize() * 0.5f, (float) rigidBodyBox.getYsize() * 0.5f, (float) rigidBodyBox.getZsize() * 0.5f));
//                    final var v8 = Convert.toMinecraft(new Vector3f((float) -rigidBodyBox.getXsize() * 0.5f, (float) rigidBodyBox.getYsize() * 0.5f, (float) rigidBodyBox.getZsize() * 0.5f));
//
//                    v1.transform(rotation);
//                    v2.transform(rotation);
//                    v3.transform(rotation);
//                    v4.transform(rotation);
//                    v5.transform(rotation);
//                    v6.transform(rotation);
//                    v7.transform(rotation);
//                    v8.transform(rotation);
//
//                    var f = 1f/8f;
//
//                    v1.add(location);
//                    v2.add(location);
//                    v3.add(location);
//                    v4.add(location);
//                    v5.add(location);
//                    v6.add(location);
//                    v7.add(location);
//                    v8.add(location);
//
//                    final var v1y = v1.y();
//                    final var v2y = v2.y();
//                    final var v3y = v3.y();
//                    final var v4y = v4.y();
//                    final var v5y = v5.y();
//                    final var v6y = v6.y();
//                    final var v7y = v7.y();
//                    final var v8y = v8.y();
//                    final var avg = (v1y + v2y + v3y + v4y + v5y + v6y + v7y + v8y) * f;
//
//                    v1.sub(location);
//                    v2.sub(location);
//                    v3.sub(location);
//                    v4.sub(location);
//                    v5.sub(location);
//                    v6.sub(location);
//                    v7.sub(location);
//                    v8.sub(location);
//
////                    rigidBody.applyCentralForce(new Vector3f(0, force * 0.75f, 0));
//                    rigidBody.applyForce(new Vector3f(0.0f, force * f * (v1y / avg), 0.0f), Convert.toBullet(v1));
//                    rigidBody.applyForce(new Vector3f(0.0f, force * f * (v2y / avg), 0.0f), Convert.toBullet(v2));
//                    rigidBody.applyForce(new Vector3f(0.0f, force * f * (v3y / avg), 0.0f), Convert.toBullet(v3));
//                    rigidBody.applyForce(new Vector3f(0.0f, force * f * (v4y / avg), 0.0f), Convert.toBullet(v4));
//                    rigidBody.applyForce(new Vector3f(0.0f, force * f * (v5y / avg), 0.0f), Convert.toBullet(v5));
//                    rigidBody.applyForce(new Vector3f(0.0f, force * f * (v6y / avg), 0.0f), Convert.toBullet(v6));
//                    rigidBody.applyForce(new Vector3f(0.0f, force * f * (v7y / avg), 0.0f), Convert.toBullet(v7));
//                    rigidBody.applyForce(new Vector3f(0.0f, force * f * (v8y / avg), 0.0f), Convert.toBullet(v8));

	        /*
	        depth (m) * water density (kg/m3) * gravity (m/s2) = pressure (Pa)
            pressure (Pa) * area (m2) = force (N)
	        */

    public static void applyDragForce(ElementRigidBody rigidBody, Vector3f location, float fluidDensity) {
        final var box = rigidBody.getCollisionShape().boundingBox(rigidBody.getPhysicsLocation(null), new Quaternion(), null);
        final var area = (float) Math.pow(Convert.toMinecraft(box).getSize(), 2);
        final var v2 = rigidBody.getLinearVelocity(null).lengthSquared();
        final var linearDirection = rigidBody.getLinearVelocity(null).normalize();
        final var dragCoefficient = rigidBody.getDragCoefficient();

        /* 0.5CpAv2
           C = drag coefficient of object
           p = density of fluid
           A = area of object
           v2 = velocity squared of object */
        final var force = new Vector3f().set(linearDirection).multLocal(0, -0.5f * dragCoefficient * fluidDensity * area * v2, 0);

        if (Float.isFinite(force.lengthSquared()) && force.lengthSquared() > 0) {
//            rigidBody.applyCentralForce(force);
            rigidBody.applyForce(force, location);
        }
    }
}