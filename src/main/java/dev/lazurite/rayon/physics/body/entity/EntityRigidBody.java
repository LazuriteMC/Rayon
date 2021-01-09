package dev.lazurite.rayon.physics.body.entity;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import net.minecraft.entity.Entity;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * A basic {@link RigidBody} class representing an {@link Entity}.
 * @see DynamicBodyEntity
 */
public abstract class EntityRigidBody extends RigidBody {
    protected final Entity entity;

    public EntityRigidBody(Entity entity, RigidBodyConstructionInfo info) {
        super(info);
        this.entity = entity;
    }

    public void setOrientation(Quat4f orientation) {
        worldTransform.setRotation(orientation);
    }

    public void setPosition(Vector3f position) {
        worldTransform.origin.set(position);
        entity.pos = VectorHelper.vector3fToVec3d(position);
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityRigidBody) {
            return ((EntityRigidBody) obj).getEntity().equals(getEntity());
        }

        return false;
    }
}
