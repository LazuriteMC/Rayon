package dev.lazurite.rayon.core.impl.physics.space.body;

import com.jme3.bounding.BoundingBox;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.physics.space.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.core.impl.physics.space.body.type.DebuggableBody;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.util.Clump;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.debug.DebugLayer;
import dev.lazurite.rayon.core.impl.util.math.Frame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
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
 * @see MinecraftSpace
 */
public class ElementRigidBody extends PhysicsRigidBody implements DebuggableBody {
    private final PhysicsElement element;
    private final MinecraftSpace space;
    private boolean propertiesDirty;
    private int envLoadDistance;
    private float dragCoefficient;

    private boolean doFluidResistance = true;
    private boolean doTerrainLoading = true;
    private boolean doEntityLoading = true;

    private PlayerEntity priorityPlayer;
    private final Frame frame = new Frame();
    private Clump clump;

    public ElementRigidBody(PhysicsElement element, MinecraftSpace space, CollisionShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(shape, mass);
        this.element = element;
        this.space = space;
        this.setDragCoefficient(dragCoefficient);
        this.setFriction(friction);
        this.setRestitution(restitution);
        this.setEnvironmentLoadDistance(calculateLoadDistance());
    }

    public ElementRigidBody(PhysicsElement element, MinecraftSpace space, CollisionShape shape) {
        this(element, space, shape, 1.0f, 0.05f, 1.0f, 0.5f);
    }

    /**
     * The simplest way to create a new {@link ElementRigidBody}.
     * Only works if the {@link PhysicsElement} is an {@link Entity}.
     * @param entity the element to base this body around
     */
    public ElementRigidBody(Entity entity) {
        this((PhysicsElement) entity, MinecraftSpace.get(entity.getEntityWorld()), new BoundingBoxShape(entity.getBoundingBox()));
    }

    /**
     * Calculates the distance away blocks should be loaded based
     * on the size of the collision bounding box.
     * @return the max distance to load blocks from
     */
    protected int calculateLoadDistance() {
        return (int) boundingBox(new BoundingBox()).getExtent(new Vector3f()).length() + 1;
    }

    @Override
    public void setCollisionShape(CollisionShape collisionShape) {
        super.setCollisionShape(collisionShape);
        this.setEnvironmentLoadDistance(calculateLoadDistance());
    }

    public PhysicsElement getElement() {
        return this.element;
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }

    public void setPropertiesDirty(boolean propertiesDirty) {
        this.propertiesDirty = propertiesDirty;
    }

    public boolean arePropertiesDirty() {
        return this.propertiesDirty;
    }

    public Frame getFrame() {
        return this.frame;
    }

    public Clump getClump() {
        return this.clump;
    }

    public void setClump(Clump clump) {
        this.clump = clump;
    }

    @Override
    public Vector3f getOutlineColor() {
        return new Vector3f(1.0f, 0.6f, 0);
    }

    @Override
    public DebugLayer getDebugLayer() {
        return DebugLayer.BODY;
    }

    public boolean needsMovementUpdate() {
        if (getFrame() != null) {
            return getFrame().getLocationDelta(new Vector3f()).length() > 0.1f ||
                    getFrame().getRotationDelta(new Vector3f()).length() > 0.01f;
        }

        return false;
    }

    public static boolean canCollideWith(Entity entity) {
        return (entity instanceof BoatEntity || entity instanceof MinecartEntity || entity instanceof LivingEntity) && !(entity instanceof PhysicsElement);
    }

    /* Property Setters */

    @Override
    public void setMass(float mass) {
        super.setMass(mass);
        this.setPropertiesDirty(true);
    }

    public void setDragCoefficient(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
        this.setPropertiesDirty(true);
    }

    @Override
    public void setFriction(float friction) {
        super.setFriction(friction);
        this.setPropertiesDirty(true);
    }

    @Override
    public void setRestitution(float restitution) {
        super.setRestitution(restitution);
        this.setPropertiesDirty(true);
    }

    public void setEnvironmentLoadDistance(int envLoadDistance) {
        this.envLoadDistance = envLoadDistance;
        this.setPropertiesDirty(true);
    }

    public void setDoFluidResistance(boolean doFluidResistance) {
        this.doFluidResistance = doFluidResistance;
        this.setPropertiesDirty(true);
    }

    public void setDoTerrainLoading(boolean doTerrainLoading) {
        this.doTerrainLoading = doTerrainLoading;
        this.setPropertiesDirty(true);
    }

    public void setDoEntityLoading(boolean doEntityLoading) {
        this.doEntityLoading = doEntityLoading;
        this.setPropertiesDirty(true);
    }

    public void prioritize(@Nullable PlayerEntity player) {
        priorityPlayer = player;
        this.setPropertiesDirty(true);
    }

    /*
     * Property Getters
     *     getMass()
     *     getFriction()
     *     getRestitution()
     */

    public PlayerEntity getPriorityPlayer() {
        return this.priorityPlayer;
    }

    public float getDragCoefficient() {
        return dragCoefficient;
    }

    public int getEnvironmentLoadDistance() {
        return this.envLoadDistance;
    }

    public boolean shouldDoFluidResistance() {
        return this.doFluidResistance;
    }

    public boolean shouldDoTerrainLoading() {
        return this.doTerrainLoading;
    }

    public boolean shouldDoEntityLoading() {
        return this.doEntityLoading;
    }
}
