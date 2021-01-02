package dev.lazurite.rayon.physics.entity;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.physics.Rayon;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public abstract class EntityRigidBody extends RigidBody implements ComponentV3, AutoSyncedComponent, CommonTickingComponent {
    protected final Entity entity;

    public EntityRigidBody(Entity entity, RigidBodyConstructionInfo info) {
        super(info);
        this.entity = entity;
    }

    public static EntityRigidBody get(Entity entity) {
        try {
            return Rayon.PHYSICS_ENTITY.get(entity);
        } catch (Exception e) {
            return null;
        }
    }

    public abstract void step(float delta);

    public void setOrientation(Quat4f orientation) {
        Transform trans = getWorldTransform(new Transform());
        trans.setRotation(orientation);
        setWorldTransform(trans);
    }

    public void setPosition(Vector3f position) {
        Transform trans = getWorldTransform(new Transform());
        trans.origin.set(position);
        setWorldTransform(trans);
        entity.pos = VectorHelper.vector3fToVec3d(position);
    }

    public void setLinearVelocity(Vector3f linearVelocity) {

    }

    public void setAngularVelocity(Vector3f angularVelocity) {

    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        setOrientation(QuaternionHelper.fromTag(tag.getCompound("orientation")));
        setPosition(VectorHelper.fromTag(tag.getCompound("position")));
        setLinearVelocity(VectorHelper.fromTag(tag.getCompound("linearVelocity")));
        setAngularVelocity(VectorHelper.fromTag(tag.getCompound("angularVelocity")));
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.put("orientation", QuaternionHelper.toTag(getOrientation(new Quat4f())));
        tag.put("position", VectorHelper.toTag(getCenterOfMassPosition(new Vector3f())));
        tag.put("linear_velocity", VectorHelper.toTag(getLinearVelocity(new Vector3f())));
        tag.put("angular_velocity", VectorHelper.toTag(getAngularVelocity(new Vector3f())));
    }
}
