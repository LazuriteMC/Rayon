package dev.lazurite.rayon.impl.bullet.collision.body;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.rayon.impl.util.Frame;
import dev.lazurite.rayon.impl.util.debug.Debuggable;
import com.jme3.math.Vector3f;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;

public abstract class ElementRigidBody extends PhysicsRigidBody implements Debuggable {
    protected final PhysicsElement element;
    protected final MinecraftSpace space;

    private final Frame frame;
    private boolean terrainLoading;
    private float dragCoefficient;
    private BuoyancyType buoyancyType;
    private DragType dragType;

    public ElementRigidBody(PhysicsElement element, MinecraftSpace space, MinecraftShape.Convex shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(shape, mass);
        this.space = space;
        this.element = element;
        this.frame = new Frame();

        this.setTerrainLoadingEnabled(!this.isStatic());
        this.setDragCoefficient(dragCoefficient);
        this.setFriction(friction);
        this.setRestitution(restitution);
        this.setBuoyancyType(BuoyancyType.ALL);
        this.setDragType(DragType.ALL);
    }

    public PhysicsElement getElement() {
        return this.element;
    }

    public void readTagInfo(CompoundTag tag) {
        this.setPhysicsRotation(Convert.toBullet(QuaternionHelper.fromTag(tag.getCompound("orientation"))));
        this.setLinearVelocity(Convert.toBullet(VectorHelper.fromTag(tag.getCompound("linearVelocity"))));
        this.setAngularVelocity(Convert.toBullet(VectorHelper.fromTag(tag.getCompound("angularVelocity"))));
//        this.setMass(tag.getFloat("mass"));
//        this.setDragCoefficient(tag.getFloat("dragCoefficient"));
//        this.setFriction(tag.getFloat("friction"));
//        this.setRestitution(tag.getFloat("restitution"));
//        this.setBuoyancyType(ElementRigidBody.BuoyancyType.values()[tag.getInt("buoyancyType")]);
//        this.setDragType(ElementRigidBody.DragType.values()[tag.getInt("dragType")]);
    }

    public boolean terrainLoadingEnabled() {
        return this.terrainLoading && !this.isStatic();
    }

    public void setTerrainLoadingEnabled(boolean terrainLoading) {
        this.terrainLoading = terrainLoading;
    }

    public float getDragCoefficient() {
        return dragCoefficient;
    }

    public void setDragCoefficient(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    public BuoyancyType getBuoyancyType() {
        return this.buoyancyType;
    }

    public void setBuoyancyType(BuoyancyType buoyancyType) {
        this.buoyancyType = buoyancyType;
    }

    public DragType getDragType() {
        return this.dragType;
    }

    public void setDragType(DragType dragType) {
        this.dragType = dragType;
    }

    public Frame getFrame() {
        return this.frame;
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }

    @Override
    public Vector3f getOutlineColor() {
        return this.isActive() ? new Vector3f(1.0f, 1.0f, 1.0f) : new Vector3f(1.0f, 0.0f, 0.0f);
    }

    @Override
    public float getOutlineAlpha() {
        return 1.0f;
    }

    public void updateFrame() {
        getFrame().from(getFrame(), getPhysicsLocation(new Vector3f()), getPhysicsRotation(new Quaternion()));
    }

    public boolean isNear(BlockPos blockPos) {
        return Convert.toMinecraft(this.boundingBox(new BoundingBox())).intersects(new AABB(blockPos).inflate(0.5f));
    }

    @Override
    public MinecraftShape.Convex getCollisionShape() {
        return (MinecraftShape.Convex) super.getCollisionShape();
    }

    public boolean isWaterBuoyancyEnabled() {
        return buoyancyType == BuoyancyType.WATER || buoyancyType == BuoyancyType.ALL;
    }

    public boolean isAirBuoyancyEnabled() {
        return buoyancyType == BuoyancyType.AIR || buoyancyType == BuoyancyType.ALL;
    }

    public boolean isWaterDragEnabled() {
        return dragType == DragType.WATER || dragType == DragType.ALL;
    }

    public boolean isAirDragEnabled() {
        return dragType == DragType.AIR || dragType == DragType.ALL;
    }

    public enum BuoyancyType {
        NONE,
        AIR,
        WATER,
        ALL
    }

    public enum DragType {
        NONE,
        AIR,
        WATER,
        ALL
    }
}