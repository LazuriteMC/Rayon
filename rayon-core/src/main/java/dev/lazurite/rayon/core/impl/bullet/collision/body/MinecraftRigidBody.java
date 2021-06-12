package dev.lazurite.rayon.core.impl.bullet.collision.body;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.util.model.Clump;
import dev.lazurite.rayon.core.impl.util.model.Frame;

public abstract class MinecraftRigidBody extends PhysicsRigidBody {
    protected final MinecraftSpace space;

    private final Frame frame = new Frame();
    private boolean doTerrainLoading;
    private float dragCoefficient;
    private Clump clump;

    public MinecraftRigidBody(MinecraftSpace space, MinecraftShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(shape, mass);
        this.space = space;
        this.setDragCoefficient(dragCoefficient);
        this.setFriction(friction);
        this.setRestitution(restitution);
        this.setDoTerrainLoading(!this.isStatic());
    }

    public boolean shouldDoTerrainLoading() {
        return this.doTerrainLoading && !this.isStatic();
    }

    public void setDoTerrainLoading(boolean doTerrainLoading) {
        this.doTerrainLoading = doTerrainLoading;
    }

    public float getDragCoefficient() {
        return dragCoefficient;
    }

    public void setDragCoefficient(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    public Clump getClump() {
        return this.clump;
    }

    public void setClump(Clump clump) {
        this.clump = clump;
    }

    public Vector3f getOutlineColor() {
        return new Vector3f(1.0f, 1.0f, 1.0f);
    }

    public float getOutlineAlpha() {
        return 0.5f;
    }

    public void updateFrame() {
        getFrame().from(getFrame(),
                getPhysicsLocation(new Vector3f()),
                getPhysicsRotation(new Quaternion()),
                getCollisionShape().boundingBox(new Vector3f(), new Quaternion(), new BoundingBox()));
    }

    public Frame getFrame() {
        return this.frame;
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }

    @Override
    public MinecraftShape getCollisionShape() {
        return (MinecraftShape) super.getCollisionShape();
    }
}
