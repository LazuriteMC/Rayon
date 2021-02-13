package dev.lazurite.rayon.impl.element;

import com.jme3.math.Quaternion;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.impl.bullet.body.type.DebuggableBody;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.body.type.CustomDragBody;
import dev.lazurite.rayon.impl.bullet.body.type.TerrainLoadingBody;
import dev.lazurite.rayon.impl.bullet.manager.DebugManager;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ElementRigidBody extends PhysicsRigidBody implements CustomDragBody, TerrainLoadingBody, DebuggableBody {
    private final PhysicsElement element;
    private float dragCoefficient;
    private SyncMode syncMode = SyncMode.SERVER;
    private UUID priorityPlayer;

    public final Quaternion prevRotation = new Quaternion();
    public final Quaternion tickRotation = new Quaternion();
    public final Vector3f prevLocation = new Vector3f();
    public final Vector3f tickLocation = new Vector3f();

    public ElementRigidBody(PhysicsElement element, CollisionShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(shape, mass);
        this.element = element;
        this.setDragCoefficient(dragCoefficient);
        this.setFriction(friction);
        this.setRestitution(restitution);
    }

    public ElementRigidBody(PhysicsElement entity, CollisionShape shape) {
        this(entity, shape, 1.0f, 0.05f, 1.0f, 0.5f);
    }

    public ElementRigidBody(PhysicsElement entity) {
        this(entity, new BoundingBoxShape(entity.asEntity().getBoundingBox()));
    }

    public void prioritize(@Nullable PlayerEntity player) {
        if (player == null) {
            syncMode = SyncMode.SERVER;
        } else {
            this.priorityPlayer = player.getUuid();
            this.syncMode = SyncMode.CLIENT;
        }
    }

    public SyncMode getSyncMode() {
        return this.syncMode;
    }

    public void fromTag(CompoundTag tag) {
        setPhysicsRotation(QuaternionHelper.fromTag(tag.getCompound("orientation")));
        setPhysicsLocation(VectorHelper.fromTag(tag.getCompound("position")));
        setLinearVelocity(VectorHelper.fromTag(tag.getCompound("linear_velocity")));
        setAngularVelocity(VectorHelper.fromTag(tag.getCompound("angular_velocity")));
        setDragCoefficient(tag.getFloat("drag_coefficient"));
        setMass(tag.getFloat("mass"));
    }

    public void toTag(CompoundTag tag) {
        tag.put("orientation", QuaternionHelper.toTag(getPhysicsRotation(new Quaternion())));
        tag.put("position", VectorHelper.toTag(getPhysicsLocation(new Vector3f())));
        tag.put("linear_velocity", VectorHelper.toTag(getLinearVelocity(new Vector3f())));
        tag.put("angular_velocity", VectorHelper.toTag(getAngularVelocity(new Vector3f())));
        tag.putFloat("drag_coefficient", getDragCoefficient());
        tag.putFloat("mass", getMass());
    }

    public void setDragCoefficient(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    @Override
    public float getDragCoefficient() {
        return dragCoefficient;
    }

    public PhysicsElement getElement() {
        return this.element;
    }

    @Override
    public BlockPos getBlockPos() {
        Vector3f pos = getPhysicsLocation(new Vector3f());
        return new BlockPos(pos.x, pos.y, pos.z);
    }

    @Override
    public Vector3f getOutlineColor() {
        return new Vector3f(1.0f, 0.6f, 0);
    }

    @Override
    public DebugManager.DebugLayer getDebugLayer() {
        return DebugManager.DebugLayer.ENTITY;
    }

    public enum SyncMode {
        CLIENT,
        SERVER
    }
}
