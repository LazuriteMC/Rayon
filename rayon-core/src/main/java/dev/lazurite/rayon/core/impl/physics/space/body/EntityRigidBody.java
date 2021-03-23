package dev.lazurite.rayon.core.impl.physics.space.body;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.space.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.core.impl.physics.space.body.type.DebuggableBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import net.minecraft.entity.Entity;

/**
 * A basic {@link PhysicsRigidBody} class representing a regular entity.
 */
public class EntityRigidBody extends PhysicsRigidBody implements DebuggableBody {
    private final Entity entity;

    public EntityRigidBody(Entity entity) {
        super(new BoundingBoxShape(entity.getBoundingBox()), PhysicsRigidBody.massForStatic);
        this.entity = entity;
        this.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos().add(0, entity.getBoundingBox().getYLength() / 2.0, 0)));
        this.setPhysicsRotation(QuaternionHelper.rotateY(new Quaternion(), -entity.yaw));
    }

    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public Vector3f getOutlineColor() {
        return new Vector3f(1, 1, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityRigidBody) {
            return ((EntityRigidBody) obj).getEntity().equals(getEntity());
        }

        return false;
    }
}
