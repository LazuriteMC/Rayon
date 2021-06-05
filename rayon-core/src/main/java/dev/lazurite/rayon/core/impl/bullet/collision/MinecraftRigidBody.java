package dev.lazurite.rayon.core.impl.bullet.collision;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.util.model.Clump;
import dev.lazurite.rayon.core.impl.util.model.Frame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;

public abstract class MinecraftRigidBody extends PhysicsRigidBody {
    private float dragCoefficient;
    private int envLoadDistance;

    private boolean doTerrainLoading = true;
    private boolean doFluidResistance = true;

    protected final MinecraftSpace space;
    private Clump clump;

    private final Frame frame = new Frame();
    private boolean shouldResetFrame;

    public MinecraftRigidBody(MinecraftSpace space, MinecraftShape shape, float mass, float dragCoefficient, float friction, float restitution) {
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

    public MinecraftSpace getSpace() {
        return this.space;
    }

    @Override
    public MinecraftShape getCollisionShape() {
        return (MinecraftShape) super.getCollisionShape();
    }

    public static boolean canCollideWith(Entity entity) {
        return (entity instanceof BoatEntity || entity instanceof MinecartEntity || entity instanceof LivingEntity) && !(entity instanceof PhysicsElement);
    }
}
