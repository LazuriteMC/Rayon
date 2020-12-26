package dev.lazurite.rayon.physics.component.entity;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import dev.lazurite.rayon.physics.PhysicsWorld;
import dev.lazurite.rayon.physics.helper.BodyHelper;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.physics.util.Body;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.UUID;

public class DynamicPhysicsComponent implements PhysicsEntityComponent {
    private final Entity entity;
    private RigidBody body;

    private UUID owner;

    public DynamicPhysicsComponent(@NotNull Entity entity) {
        this.entity = entity;
        this.body = BodyHelper.create(entity, new BoxShape(new Vector3f(1, 1, 1)), 1.0f);

        PhysicsWorld.INSTANCE.track(entity);
    }

    @Override
    public void step(float delta) {

    }

    @Override
    public void tick() {
        entity.setVelocity(0, 0.1, 0);
    }

    @Override
    public void setOrientation(Quat4f orientation) {

    }

    @Override
    public void setPosition(Vector3f position) {

    }

    @Override
    public void setLinearVelocity(Vector3f linearVelocity) {

    }

    @Override
    public void setAngularVelocity(Vector3f angularVelocity) {

    }

    @Override
    public Quat4f getOrientation() {
        return body.getOrientation(new Quat4f());
    }

    @Override
    public Vector3f getPosition() {
        return body.getCenterOfMassPosition(new Vector3f());
    }

    @Override
    public Vector3f getLinearVelocity() {
        return body.getLinearVelocity(new Vector3f());
    }

    @Override
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
