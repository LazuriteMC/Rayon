package dev.lazurite.rayon.impl.bullet.body.type;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

/**
 * Any {@link PhysicsRigidBody} with this interface assigned will be subject
 * to fluid resistance using it's drag coefficient value. The calculations
 * done here are approximate and don't model the real world super closely.
 */
public interface FluidDragBody {
    float getDragCoefficient();
    boolean shouldDoFluidResistance();
    void setDoFluidResistance(boolean doFluidResistance);

    default void applyDrag(BlockView world) {
        assert this instanceof PhysicsRigidBody : "Drag body must be rigid body.";

        if (shouldDoFluidResistance()) {
            PhysicsRigidBody rigidBody = (PhysicsRigidBody) this;
            float drag;

            Fluid fluid = world.getFluidState(
                    new BlockPos(VectorHelper.vector3fToVec3d(
                            rigidBody.boundingBox(new BoundingBox())
                                    .getMax(new Vector3f())))).getFluid();

            if (Fluids.LAVA.equals(fluid)) {
                drag = Config.getInstance().getLavaDensity();
            } else if (Fluids.WATER.equals(fluid)) {
                drag = Config.getInstance().getWaterDensity();
            } else {
                drag = Config.getInstance().getAirDensity();
            }

            float dragCoefficient = getDragCoefficient();
            float gravitationalForce = rigidBody.getMass() * Config.getInstance().getGravity();
            float area = (float) Math.pow(rigidBody.boundingBox(new BoundingBox()).getExtent(new Vector3f()).lengthSquared(), 2);
            float k = (drag * dragCoefficient * area) / 2.0f;

            Vector3f force = new Vector3f()
                    .set(rigidBody.getLinearVelocity(new Vector3f()))
                    .multLocal(-rigidBody.getLinearVelocity(new Vector3f()).lengthSquared())
                    .multLocal(k);

            if (force.y > -gravitationalForce) {
                /* Makes the object stop when it collides with a more dense liquid */
                rigidBody.applyCentralImpulse(rigidBody.getLinearVelocity(new Vector3f()).multLocal(-rigidBody.getMass()));
            } else {
                rigidBody.applyCentralForce(force);
            }
        }
    }
}
