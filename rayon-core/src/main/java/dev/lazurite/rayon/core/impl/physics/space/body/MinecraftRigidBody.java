package dev.lazurite.rayon.core.impl.physics.space.body;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.debug.DebugLayer;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.util.Clump;
import dev.lazurite.rayon.core.impl.util.math.Frame;

public abstract class MinecraftRigidBody extends PhysicsRigidBody {
    private float dragCoefficient;
    private int envLoadDistance;

    private boolean doTerrainLoading = true;
    private boolean doFluidResistance = true;

    protected final MinecraftSpace space;
    private Clump clump;

    private final Frame frame = new Frame();
    private boolean shouldResetFrame;

    public MinecraftRigidBody(MinecraftSpace space, CollisionShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(shape, mass);
        this.space = space;
        this.setDragCoefficient(dragCoefficient);
        this.setFriction(friction);
        this.setRestitution(restitution);
        this.setEnvironmentLoadDistance(calculateLoadDistance());
    }

    /**
     * Calculates the distance away blocks should be loaded based
     * on the size of the collision bounding box.
     *
     * @return the max distance to load blocks from
     */
    protected int calculateLoadDistance() {
        return (int) boundingBox(new BoundingBox()).getExtent(new Vector3f()).length() + 1;
    }

    public int getEnvironmentLoadDistance() {
        return this.envLoadDistance;
    }

    public boolean shouldDoTerrainLoading() {
        return this.doTerrainLoading;
    }

    public void setEnvironmentLoadDistance(int envLoadDistance) {
        this.envLoadDistance = envLoadDistance;
    }

    public void setDoTerrainLoading(boolean doTerrainLoading) {
        this.doTerrainLoading = doTerrainLoading;
    }

    public boolean shouldDoFluidResistance() {
        return this.doFluidResistance;
    }

    public float getDragCoefficient() {
        return dragCoefficient;
    }

    public void setDoFluidResistance(boolean doFluidResistance) {
        this.doFluidResistance = doFluidResistance;
    }

    public void setDragCoefficient(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    public float getVolume() {
        BoundingBox box = getCollisionShape().boundingBox(new Vector3f(), new Quaternion(), new BoundingBox());
        return box.getXExtent() * box.getYExtent() * box.getZExtent();
    }

    public float getPartialVolume(float differential) {
        BoundingBox box = getCollisionShape().boundingBox(new Vector3f(), new Quaternion(), new BoundingBox());
        return box.getXExtent() * (box.getYExtent() * differential) * box.getZExtent();
    }

    @Override
    public void setCollisionShape(CollisionShape collisionShape) {
        super.setCollisionShape(collisionShape);
        this.setEnvironmentLoadDistance(calculateLoadDistance());
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

    public DebugLayer getDebugLayer() {
        return DebugLayer.BLOCK;
    }

    public void scheduleFrameReset() {
        this.shouldResetFrame = true;
    }

    public Frame updateFrame() {
        getFrame().from(getFrame(),
                getPhysicsLocation(new Vector3f()),
                getPhysicsRotation(new Quaternion()),
                getCollisionShape().boundingBox(new Vector3f(), new Quaternion(), new BoundingBox()));

        if (shouldResetFrame) {
            getFrame().reset();
            this.shouldResetFrame = false;
        }

        return getFrame();
    }

    public Frame getFrame() {
        return this.frame;
    }
}
