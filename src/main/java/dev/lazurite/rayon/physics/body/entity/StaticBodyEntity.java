package dev.lazurite.rayon.physics.body.entity;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.shape.BoundingBoxShape;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class StaticBodyEntity extends EntityRigidBody {
    private StaticBodyEntity(Entity entity, RigidBodyConstructionInfo info) {
        super(entity, info);
    }

    public static StaticBodyEntity create(Entity entity) {
        /* Create the entity's shape */
        CollisionShape collisionShape = new BoundingBoxShape(entity.getBoundingBox());

        /* Get the position of the entity. */
        Vector3f position = VectorHelper.vec3dToVector3f(entity.getPos());

        /* Calculate the new motion state. */
        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 1, 0, 0), position, 1.0f)));

        /* Create the Body based on the construction info. */
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(0, motionState, collisionShape, new Vector3f());
        StaticBodyEntity physics = new StaticBodyEntity(entity, constructionInfo);
        physics.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        return physics;
    }

    @Override
    public void step(float delta) {
        setPosition(VectorHelper.vec3dToVector3f(entity.getPos().add(new Vec3d(0, entity.getBoundingBox().getYLength() / 2.0, 0))));
        setOrientation(QuaternionHelper.rotateY(new Quat4f(0, 1, 0, 0), -entity.yaw));
    }
}
