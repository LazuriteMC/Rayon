package dev.lazurite.rayon.physics.rigidbody.entity;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import dev.lazurite.rayon.physics.rigidbody.SteppableBody;
import net.minecraft.entity.Entity;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public abstract class EntityRigidBody extends RigidBody implements SteppableBody {
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
