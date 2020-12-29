package dev.lazurite.rayon.physics.helper;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.physics.util.BodyType;
import dev.lazurite.rayon.physics.util.TypedRigidBody;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class BodyHelper {
    public static CollisionShape getBoundingBoxShape(Box box) {
        return new BoxShape(new Vector3f(
                (float) (box.maxX - box.minX) / 2.0f,
                (float) (box.maxY - box.minY) / 2.0f,
                (float) (box.maxZ - box.minZ) / 2.0f
        ));
    }

    public static TypedRigidBody create(Entity entity, @Nullable CollisionShape collisionShape, float mass) {
        /* Create a BoxShape if a shape isn't passed in. */
        if (collisionShape == null) {
            collisionShape = getBoundingBoxShape(entity.getBoundingBox());
        }

        /* Calculate the inertia of the shape. */
        Vector3f inertia = new Vector3f(0.0F, 0.0F, 0.0F);
        collisionShape.calculateLocalInertia(mass, inertia);

        /* Get the position of the entity. */
        Vector3f position = VectorHelper.vec3dToVector3f(entity.getPos());

        /* Calculate the new motion state. */
        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 1, 0, 0), position, mass)));

        /* Create the Body based on the construction info. */
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(mass, motionState, collisionShape, inertia);
        TypedRigidBody body = new TypedRigidBody(constructionInfo, BodyType.ENTITY);

        /* Set the activation state so that deactivation is disabled. */
        body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        return body;
    }

    public static TypedRigidBody create(BlockPos blockPos, CollisionShape shape, float friction) {
        /* Set the position of the rigid body to the block's position */
        Vector3f position = new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(), position, friction)));

        /* Set up the rigid body's construction info and initialization */
        RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(0, motionState, shape, new Vector3f(0, 0, 0));
        TypedRigidBody body = new TypedRigidBody(ci, BodyType.BLOCK);

        /* Add it to the necessary locations */
        return body;
    }
}
