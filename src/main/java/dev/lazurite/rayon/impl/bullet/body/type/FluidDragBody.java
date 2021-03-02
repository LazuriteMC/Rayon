package dev.lazurite.rayon.impl.bullet.body.type;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.util.RayonException;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Any {@link PhysicsRigidBody} with this interface assigned will be subject
 * to fluid resistance using it's drag coefficient value. The calculations
 * done here are approximate and don't model the real world super closely.
 */
public interface FluidDragBody {
    float getDragCoefficient();
    boolean shouldDoFluidResistance();
    void setDoFluidResistance(boolean doFluidResistance);

    default void applyDrag(MinecraftSpace space) {
        if (!(this instanceof PhysicsRigidBody)) {
            throw new RayonException("Drag body must be rigid body");
        }

        if (shouldDoFluidResistance()) {
            World world = space.getWorld();
            PhysicsRigidBody rigidBody = (PhysicsRigidBody) this;
            float drag;

            BlockPos blockPos= new BlockPos(VectorHelper.vector3fToVec3d(
                    rigidBody.boundingBox(new BoundingBox())
                            .getMax(new Vector3f())));

            BlockView chunk = world.getChunkManager().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
            Block block = Blocks.AIR;

            if (chunk != null) {
                block = chunk.getBlockState(blockPos).getBlock();
            }

            if (Blocks.LAVA.equals(block)) {
                drag = space.getLavaDensity();
            } else if (Blocks.WATER.equals(block)) {
                drag = space.getWaterDensity();
            } else {
                drag = space.getAirDensity();
            }

            float dragCoefficient = getDragCoefficient();
            float gravitationalForce = rigidBody.getMass() * space.getGravity(new Vector3f()).length();
            float area = (float) Math.pow(rigidBody.boundingBox(new BoundingBox()).getExtent(new Vector3f()).lengthSquared(), 2);
            float k = (drag * dragCoefficient * area) / 2.0f;

            Vector3f force = new Vector3f()
                    .set(rigidBody.getLinearVelocity(new Vector3f()))
                    .multLocal(-rigidBody.getLinearVelocity(new Vector3f()).lengthSquared())
                    .multLocal(k);

            if (drag != space.getAirDensity() && force.y > -gravitationalForce) {
                /* Makes the object stop when it collides with a more dense liquid */
                rigidBody.applyCentralImpulse(rigidBody.getLinearVelocity(new Vector3f()).multLocal(-rigidBody.getMass()));
            } else {
                rigidBody.applyCentralForce(force);
            }
        }
    }
}
