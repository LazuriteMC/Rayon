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
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public abstract class ElementRigidBody extends PhysicsRigidBody implements Debuggable {
    protected final PhysicsElement element;
    protected final MinecraftSpace space;

    private final Frame frame;
    private boolean terrainLoading;
    private boolean dragForces;
    private boolean buoyantForces;
    private float dragCoefficient;
    private DragType dragType;

    public ElementRigidBody(PhysicsElement element, MinecraftSpace space, MinecraftShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(shape, mass);
        this.space = space;
        this.element = element;
        this.frame = new Frame();

        this.setTerrainLoadingEnabled(!this.isStatic());
        this.setDragForcesEnabled(true);
        this.setBuoyantForcesEnabled(true);
        this.setDragCoefficient(dragCoefficient);
        this.setFriction(friction);
        this.setRestitution(restitution);
        this.setDragType(DragType.SIMPLE);
    }

    public PhysicsElement getElement() {
        return this.element;
    }

    public boolean terrainLoadingEnabled() {
        return this.terrainLoading && !this.isStatic();
    }

    public void setTerrainLoadingEnabled(boolean terrainLoading) {
        this.terrainLoading = terrainLoading;
    }

    public boolean buoyantForcesEnabled() {
        return this.buoyantForces;
    }

    public void setBuoyantForcesEnabled(boolean buoyantForces) {
        this.buoyantForces = buoyantForces;
    }

    public boolean dragForcesEnabled() {
        return this.dragForces && !this.isStatic();
    }

    public void setDragForcesEnabled(boolean dragForces) {
        this.dragForces = dragForces;
    }

    public float getDragCoefficient() {
        return dragCoefficient;
    }

    public void setDragCoefficient(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
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
    public MinecraftShape getCollisionShape() {
        return (MinecraftShape) super.getCollisionShape();
    }

    public enum DragType {
        SIMPLE,
        REALISTIC
    }
}