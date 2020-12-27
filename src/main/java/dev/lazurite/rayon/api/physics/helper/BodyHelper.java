package dev.lazurite.rayon.api.physics.helper;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.api.physics.helper.math.VectorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class BodyHelper {
    public static RigidBody create(Entity entity, CollisionShape collisionShape, float mass) {
        /* Create a BoxShape if a shape isn't passed in. */
        if (collisionShape == null) {
            Box bb = entity.getBoundingBox();
            collisionShape = new BoxShape(new Vector3f(
                    (float) (bb.maxX - (bb.minX / 2.0f)),
                    (float) (bb.maxY - (bb.minY / 2.0f)),
                    (float) (bb.maxZ - (bb.minZ / 2.0f))
            ));
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
        RigidBody body = new RigidBody(constructionInfo);

        /* Set the activation state so that deactivation is disabled. */
        body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        return body;
    }
}
