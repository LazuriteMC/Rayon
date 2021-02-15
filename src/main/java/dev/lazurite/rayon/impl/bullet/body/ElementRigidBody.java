package dev.lazurite.rayon.impl.bullet.body;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.impl.bullet.body.type.DebuggableBody;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.body.type.CustomDragBody;
import dev.lazurite.rayon.impl.bullet.body.type.TerrainLoadingBody;
import dev.lazurite.rayon.impl.bullet.thread.MinecraftSpace;
import dev.lazurite.rayon.impl.util.debug.DebugLayer;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import dev.lazurite.rayon.impl.util.math.interpolate.Frame;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents how a {@link PhysicsElement} will interact with the {@link MinecraftSpace}. The user of Rayon
 * will be asked to provide one of these within their {@link PhysicsElement} implementation since when their entity is
 * spawned, the rigid body will be added to the physics space.<br>
 * Several properties can be modified and synced between the client and the server including:
 * <ul>
 *     <li>Drag</li>
 *     <li>Mass</li>
 *     <li>Friction</li>
 *     <li>Restitution</li>
 *     <li>Priority Player</li>
 * </ul>
 * as well as movement information:
 * <ul>
 *     <li>Location</li>
 *     <li>Rotation</li>
 *     <li>Linear Velocity</li>
 *     <li>Angular Velocity</li>
 * </ul>
 * @see PhysicsElement
 * @see MinecraftSpace
 */
public class ElementRigidBody extends PhysicsRigidBody implements CustomDragBody, TerrainLoadingBody, DebuggableBody {
    private final PhysicsElement element;
    private float dragCoefficient;
    private PlayerEntity priorityPlayer;
    private int loadDistance;
    private Frame frame;

    public ElementRigidBody(PhysicsElement element, CollisionShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(shape, mass);
        this.element = element;
        this.setDragCoefficient(dragCoefficient);
        this.setFriction(friction);
        this.setRestitution(restitution);
        this.loadDistance = calculateLoadDistance();
    }

    public ElementRigidBody(PhysicsElement element, CollisionShape shape) {
        this(element, shape, 1.0f, 0.05f, 1.0f, 0.5f);
    }

    /**
     * The simplest way to create a new {@link ElementRigidBody}.
     * Only works if the {@link PhysicsElement} is an {@link Entity}.
     * @param element the element to base this body around
     */
    public ElementRigidBody(PhysicsElement element) {
        this(element, new BoundingBoxShape(element.asEntity().getBoundingBox()));
    }

    public void prioritize(@Nullable PlayerEntity player) {
        priorityPlayer = player;
    }

    /**
     * Calculates the distance away blocks should be loaded based
     * on the size of the collision bounding box.
     * @return the max distance to load blocks from
     */
    public int calculateLoadDistance() {
        return (int) boundingBox(new BoundingBox()).getExtent(new Vector3f()).length() + 1;
    }

    public void fromTag(CompoundTag tag) {
        /* Movement Info */
        setPhysicsRotation(QuaternionHelper.fromTag(tag.getCompound("orientation")));
        setPhysicsLocation(VectorHelper.fromTag(tag.getCompound("position")));
        setLinearVelocity(VectorHelper.fromTag(tag.getCompound("linear_velocity")));
        setAngularVelocity(VectorHelper.fromTag(tag.getCompound("angular_velocity")));

        /* Properties */
        setDragCoefficient(tag.getFloat("drag_coefficient"));
        setMass(tag.getFloat("mass"));
        setFriction(tag.getFloat("friction"));
        setRestitution(tag.getFloat("restitution"));
    }

    public void toTag(CompoundTag tag) {
        /* Movement Info */
        tag.put("orientation", QuaternionHelper.toTag(getPhysicsRotation(new Quaternion())));
        tag.put("position", VectorHelper.toTag(getPhysicsLocation(new Vector3f())));
        tag.put("linear_velocity", VectorHelper.toTag(getLinearVelocity(new Vector3f())));
        tag.put("angular_velocity", VectorHelper.toTag(getAngularVelocity(new Vector3f())));

        /* Properties */
        tag.putFloat("drag_coefficient", getDragCoefficient());
        tag.putFloat("mass", getMass());
        tag.putFloat("friction", getFriction());
        tag.putFloat("restitution", getRestitution());
    }

    @Override
    public void setCollisionShape(CollisionShape collisionShape) {
        super.setCollisionShape(collisionShape);
        this.loadDistance = calculateLoadDistance();
    }

    @Environment(EnvType.CLIENT)
    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    public void setDragCoefficient(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    public Frame getFrame() {
        return this.frame;
    }

    public PlayerEntity getPriorityPlayer() {
        return this.priorityPlayer;
    }

    public PhysicsElement getElement() {
        return this.element;
    }

    @Override
    public float getDragCoefficient() {
        return dragCoefficient;
    }

    @Override
    public int getLoadDistance() {
        return this.loadDistance;
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
    public DebugLayer getDebugLayer() {
        return DebugLayer.ENTITY;
    }
}
