package dev.lazurite.rayon.core.impl.physics.space.body;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.physics.space.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.debug.DebugLayer;
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
 * @see MinecraftSpace
 */
public class ElementRigidBody extends MinecraftRigidBody {
    private final PhysicsElement element;
    private boolean propertiesDirty;
    private PlayerEntity priorityPlayer;

    public ElementRigidBody(PhysicsElement element, MinecraftSpace space, CollisionShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(space, shape, mass, dragCoefficient, friction, restitution);
        this.element = element;
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
        super.setDragCoefficient(dragCoefficient);
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

    @Override
    public void setEnvironmentLoadDistance(int envLoadDistance) {
        super.setEnvironmentLoadDistance(envLoadDistance);
        this.setPropertiesDirty(true);
    }

    public void setDoFluidResistance(boolean doFluidResistance) {
        super.setDoFluidResistance(doFluidResistance);
        this.setPropertiesDirty(true);
    }

    @Override
    public void setDoTerrainLoading(boolean doTerrainLoading) {
        super.setDoTerrainLoading(doTerrainLoading);
        this.setPropertiesDirty(true);
    }

    public void prioritize(@Nullable PlayerEntity player) {
        priorityPlayer = player;
        this.setPropertiesDirty(true);
    }

    /*
     * Property Getters
     *     getMass()
     *     getDragCoefficient()
     *     getFriction()
     *     getRestitution()
     */

    public PlayerEntity getPriorityPlayer() {
        return this.priorityPlayer;
    }
}
