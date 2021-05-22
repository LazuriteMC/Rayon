package dev.lazurite.rayon.core.impl.physics.space.environment;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.MinecraftRigidBody;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class FluidManager {
    private final MinecraftSpace space;
    private float airDensity = 1.2f;
    private float waterDensity = 997f;
    private float lavaDensity = 3100f;

    public FluidManager(MinecraftSpace space) {
        this.space = space;
    }

    public void doResistanceOn(MinecraftRigidBody rigidBody) {
        float dragCoefficient = rigidBody.getDragCoefficient();
        float airDensity = getAirDensity();
        float area = (float) Math.pow(rigidBody.boundingBox(new BoundingBox()).getExtent(new Vector3f()).lengthSquared(), 2);
        float k = (airDensity * dragCoefficient * area) / 2.0f;
        Vector3f force = new Vector3f().set(rigidBody.getLinearVelocity(new Vector3f())).multLocal(-rigidBody.getLinearVelocity(new Vector3f()).lengthSquared()).multLocal(k);

        if (Float.isFinite(force.lengthSquared()) && force.lengthSquared() > 0.01f) {
            rigidBody.applyCentralForce(force);
        }
    }

    public void doBuoyancyOn(MinecraftRigidBody rigidBody) {
        Vector3f location = rigidBody.getPhysicsLocation(new Vector3f());
        BoundingBox box = rigidBody.boundingBox(new BoundingBox());

        Vec3d bottom = VectorHelper.vector3fToVec3d(location.subtract(new Vector3f(0, box.getYExtent(), 0)));
        Vec3d top = VectorHelper.vector3fToVec3d(location.add(new Vector3f(0, box.getYExtent(), 0)));

        FluidState bottomState = space.getWorld().getFluidState(new BlockPos(bottom));
        FluidState topState = space.getWorld().getFluidState(new BlockPos(top));

        if (bottomState != null && topState != null) {
            float volume;

            // is it submerged??
            if (bottomState.getFluid() == topState.getFluid()) {
                volume = rigidBody.getVolume();
            } else {
                BlockPos pos = new BlockPos(bottom);
                float differential = (float) (bottom.getY() / (pos.getY() + 1));
                volume = Math.abs(rigidBody.getPartialVolume(differential));
                System.out.println("Y: " + (pos.getY()+1) + ", bottom: " + bottom.getY() + ", diff: " + differential);
            }

            Fluid fluid = bottomState.getFluid();
            float density;
            if (fluid instanceof WaterFluid) {
                density = waterDensity;
            } else {
                density = airDensity;
            }

            Vector3f force = space.getGravity(new Vector3f()).multLocal(density).multLocal(volume).multLocal(-1);
            rigidBody.applyCentralForce(force);
        }
    }

    public float getAirDensity() {
        return this.airDensity;
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }
}
