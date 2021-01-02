package dev.lazurite.rayon.physics.entity;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import net.minecraft.entity.Entity;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public abstract class EntityRigidBody extends RigidBody {
    protected final Entity entity;

    public EntityRigidBody(Entity entity, RigidBodyConstructionInfo info) {
        super(info);
        this.entity = entity;
    }

    public abstract void step(float delta);

    public void setOrientation(Quat4f orientation) {
        worldTransform.setRotation(orientation);
    }

    public void setPosition(Vector3f position) {
        worldTransform.origin.set(position);
        entity.pos = VectorHelper.vector3fToVec3d(position);
    }
}
