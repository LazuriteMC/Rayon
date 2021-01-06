package dev.lazurite.rayon.physics.body.entity;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import dev.lazurite.rayon.physics.body.SteppableBody;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import net.minecraft.entity.Entity;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public abstract class EntityRigidBody extends RigidBody implements SteppableBody {
    protected final Entity entity;
    private final Vector3f lastLinearVelocity;
    private final Vector3f linearAcceleration;

    public EntityRigidBody(Entity entity, RigidBodyConstructionInfo info) {
        super(info);
        this.entity = entity;
        this.lastLinearVelocity = new Vector3f();
        this.linearAcceleration = new Vector3f();
    }

    @Override
    public void step(float delta) {
        linearAcceleration.set(VectorHelper.mul(VectorHelper.sub(getLinearVelocity(new Vector3f()), lastLinearVelocity), delta));
        lastLinearVelocity.set(getLinearVelocity(new Vector3f()));
    }

    public void setOrientation(Quat4f orientation) {
        worldTransform.setRotation(orientation);
    }

    public void setPosition(Vector3f position) {
        worldTransform.origin.set(position);
    }

    public Vector3f getLinearAcceleration(Vector3f out) {
        out.set(linearAcceleration);
        return out;
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
