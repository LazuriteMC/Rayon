package dev.lazurite.rayon.physics.entity;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.physics.Rayon;
import dev.lazurite.rayon.physics.helper.BodyHelper;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.physics.util.TypedRigidBody;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public abstract class RigidBodyEntity implements ComponentV3, AutoSyncedComponent, CommonTickingComponent {
    protected final TypedRigidBody body;
    protected final Entity entity;
    private final Quat4f prevOrientation;

    public RigidBodyEntity(@NotNull Entity entity) {
        this.entity = entity;
        this.body = BodyHelper.create(entity, null, 1.0f);
        this.prevOrientation = new Quat4f();
    }

    public static RigidBodyEntity get(Entity entity) {
        try {
            return Rayon.PHYSICS_ENTITY.get(entity);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void tick() {
        entity.pos = VectorHelper.vector3fToVec3d(getPosition());
        entity.updatePosition(getPosition().x, getPosition().y, getPosition().z);
        setPrevOrientation(getOrientation());
    }

    public abstract void step(float delta);

    public void setPrevOrientation(Quat4f prevOrientation) {
        this.prevOrientation.set(prevOrientation);
    }

    public void setOrientation(Quat4f orientation) {
        Transform trans = body.getWorldTransform(new Transform());
        trans.setRotation(orientation);
        body.setWorldTransform(trans);
    }

    public void setPosition(Vector3f position) {
//        Vector3f min = new Vector3f();
//        Vector3f max = new Vector3f();
//        body.getCollisionShape().getAabb(new Transform(), min, max);
//
//        Vector3f difference = new Vector3f();
//        difference.sub(max, min);

//        position.add(difference);
        Transform trans = body.getWorldTransform(new Transform());
        trans.origin.set(position);
        body.setWorldTransform(trans);
        entity.pos = VectorHelper.vector3fToVec3d(position);
    }

    public void setLinearVelocity(Vector3f linearVelocity) {

    }

    public void setAngularVelocity(Vector3f angularVelocity) {

    }

    public RigidBody getRigidBody() {
        return body;
    }

    public Quat4f getPrevOrientation() {
        return this.prevOrientation;
    }

    public Quat4f getOrientation() {
        return body.getOrientation(new Quat4f());
    }

    public Vector3f getPosition() {
        return body.getCenterOfMassPosition(new Vector3f());
    }

    public Vector3f getLinearVelocity() {
        return body.getLinearVelocity(new Vector3f());
    }

    public Vector3f getAngularVelocity() {
        return body.getAngularVelocity(new Vector3f());
    }

    @Override
    public void readFromNbt(CompoundTag tag) {

    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.put("orientation", QuaternionHelper.toTag(getOrientation()));
        tag.put("position", VectorHelper.toTag(getPosition()));
        tag.put("linear_velocity", VectorHelper.toTag(getLinearVelocity()));
        tag.put("angular_velocity", VectorHelper.toTag(getAngularVelocity()));
    }
}
