package dev.lazurite.rayon.physics.entity;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.api.shape.EntityShape;
import dev.lazurite.rayon.api.shape.factory.EntityShapeFactory;
import dev.lazurite.rayon.physics.Rayon;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import net.minecraft.entity.Entity;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class DynamicPhysicsEntity extends EntityRigidBody {
    private DynamicPhysicsEntity(Entity entity, RigidBodyConstructionInfo info) {
        super(entity, info);
    }

    public static <S extends EntityShape> DynamicPhysicsEntity create(Entity entity, EntityShapeFactory<S> shapeFactory, float mass) {
        /* Get the entity's shape */
        CollisionShape collisionShape = (CollisionShape) shapeFactory.create(entity);

        /* Calculate the inertia of the shape. */
        Vector3f inertia = new Vector3f();
        collisionShape.calculateLocalInertia(mass, inertia);

        /* Get the position of the entity. */
        Vector3f position = VectorHelper.vec3dToVector3f(entity.getPos());

        /* Calculate the new motion state. */
        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 1, 0, 0), position, 1.0f)));

        /* Create the Body based on the construction info. */
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(mass, motionState, collisionShape, inertia);
        DynamicPhysicsEntity physics = new DynamicPhysicsEntity(entity, constructionInfo);
        physics.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        return physics;
    }

    @Override
    public void step(float delta) {

    }

    @Override
    public void tick() {
        if (entity.getEntityWorld().isClient()) {
            Rayon.LOGGER.info("test: " + 1 / this.getInvMass());
        }

        Vector3f position = getCenterOfMassPosition(new Vector3f());
        entity.pos = VectorHelper.vector3fToVec3d(position);
        entity.updatePosition(position.x, position.y, position.z);
    }

//    @Override
//    public void readFromNbt(CompoundTag tag) {
//        super.readFromNbt(tag);
//    }
//
//    @Override
//    public void writeToNbt(CompoundTag tag) {
//        super.writeToNbt(tag);
//    }
}
