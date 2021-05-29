package dev.lazurite.rayon.core.impl.physics.space.environment;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.MinecraftRigidBody;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;

public interface FluidManager {
    float AIR_DENSITY = 1.2f;
    float WATER_DENSITY = 997f;
    float LAVA_DENSITY = 3100f;

    static Vector3f getDragForceOn(MinecraftRigidBody rigidBody) {
        var dragCoefficient = rigidBody.getDragCoefficient();
        var area = (float) Math.pow(rigidBody.boundingBox(new BoundingBox()).getExtent(new Vector3f()).lengthSquared(), 2);
        var k = (AIR_DENSITY * dragCoefficient * area) / 2.0f;
        return new Vector3f().set(rigidBody.getLinearVelocity(new Vector3f())).multLocal(-rigidBody.getLinearVelocity(new Vector3f()).lengthSquared()).multLocal(k);
    }

    static void doBuoyancyOn(MinecraftRigidBody rigidBody) {
        var space = rigidBody.getSpace();

        var location = rigidBody.getPhysicsLocation(new Vector3f());
        var box = rigidBody.boundingBox(new BoundingBox());

        var bottom = VectorHelper.vector3fToVec3d(location.subtract(new Vector3f(0, box.getYExtent(), 0)));
        var top = VectorHelper.vector3fToVec3d(location.add(new Vector3f(0, box.getYExtent(), 0)));

        var bottomState = space.getWorld().getFluidState(new BlockPos(bottom));
        var topState = space.getWorld().getFluidState(new BlockPos(top));

        if (bottomState != null && topState != null) {
            float volume;

            // is it submerged??
            if (bottomState.getFluid() == topState.getFluid()) {
                volume = rigidBody.getVolume();
            } else {
                BlockPos pos = new BlockPos(bottom);
                var differential = (float) (bottom.getY() / (pos.getY() + 1));
                volume = Math.abs(rigidBody.getPartialVolume(differential));
                System.out.println("Y: " + (pos.getY()+1) + ", bottom: " + bottom.getY() + ", diff: " + differential);
            }

            var fluid = bottomState.getFluid();

            float density;
            if (fluid instanceof WaterFluid) {
                density = WATER_DENSITY;
            } else {
                density = AIR_DENSITY;
            }

            Vector3f force = space.getGravity(new Vector3f()).multLocal(density).multLocal(volume).multLocal(-1);
            rigidBody.applyCentralForce(force);
        }
    }
}
