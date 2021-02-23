package dev.lazurite.rayon.impl.bullet.body.type;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

/**
 * Any {@link PhysicsRigidBody} with this interface assigned will be subject
 * to fluid resistance using it's drag coefficient value. The calculations
 * done here are approximate and don't model the real world super closely.
 */
public interface FluidDragBody {
    float getDragCoefficient();

    default void applyDrag(BlockView world) {
        assert this instanceof PhysicsRigidBody : "Drag body must be rigid body.";

        PhysicsRigidBody rigidBody = (PhysicsRigidBody) this;
        float dragCoefficient = getDragCoefficient();
        float area = (float) Math.pow(rigidBody.boundingBox(new BoundingBox()).getExtent(new Vector3f()).lengthSquared(), 2);

        BoundingBox box = rigidBody.boundingBox(new BoundingBox());
        BlockPos max = new BlockPos(VectorHelper.vector3fToVec3d(box.getMax(new Vector3f())));
        BlockPos min = new BlockPos(VectorHelper.vector3fToVec3d(box.getMin(new Vector3f())));

        Block maxBlock = world.getBlockState(max).getBlock();
        Block minBlock = world.getBlockState(min).getBlock();
        float drag;

        if (Blocks.LAVA.equals(minBlock) || Blocks.LAVA.equals(maxBlock)) {
            drag = Config.getInstance().getLavaDensity();
        } else if (Blocks.WATER.equals(minBlock) || Blocks.WATER.equals(maxBlock)) {
            drag = Config.getInstance().getWaterDensity();
        } else {
            drag = Config.getInstance().getAirDensity();
        }

        float k = (drag * dragCoefficient * area) / 2.0f;
        Vector3f force = new Vector3f();
        force.set(rigidBody.getLinearVelocity(new Vector3f()));
        force.multLocal(k);
        rigidBody.applyCentralForce(force.multLocal(-rigidBody.getLinearVelocity(new Vector3f()).lengthSquared()));
    }
}
