package dev.lazurite.rayon.impl.bullet.collision.body;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import net.minecraft.world.entity.player.Player;

public class EntityRigidBody extends ElementRigidBody {
    private Player priorityPlayer;
    private boolean dirtyProperties = true;

    public EntityRigidBody(EntityPhysicsElement element, MinecraftSpace space, MinecraftShape.Convex shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(element, space, shape, mass, dragCoefficient, friction, restitution);
    }

    public EntityRigidBody(EntityPhysicsElement element, MinecraftSpace space, MinecraftShape.Convex shape) {
        this(element, space, shape, 10.0f, 0.25f, 1.0f, 0.5f);
    }

    /**
     * The simplest way to create a new {@link EntityRigidBody}.
     * @param element the element to base this body around
     */
    public EntityRigidBody(EntityPhysicsElement element) {
        this(element, MinecraftSpace.get(element.cast().level), element.createShape());
    }

    @Override
    public EntityPhysicsElement getElement() {
        return (EntityPhysicsElement) super.getElement();
    }

    public Player getPriorityPlayer() {
        return this.priorityPlayer;
    }

    public boolean isPositionDirty() {
        return getFrame() != null &&
                (getFrame().getLocationDelta(new Vector3f()).length() > 0.1f ||
                getFrame().getRotationDelta(new Vector3f()).length() > 0.01f);
    }

    public boolean arePropertiesDirty() {
        return this.dirtyProperties;
    }

    public void setPropertiesDirty(boolean dirtyProperties) {
        this.dirtyProperties = dirtyProperties;
    }

    public void prioritize(Player priorityPlayer) {
        this.priorityPlayer = priorityPlayer;
        this.dirtyProperties = true;
    }

    @Override
    public void setMass(float mass) {
        super.setMass(mass);
        this.dirtyProperties = true;
    }

    @Override
    public void setDragCoefficient(float dragCoefficient) {
        super.setDragCoefficient(dragCoefficient);
        this.dirtyProperties = true;
    }

    @Override
    public void setFriction(float friction) {
        super.setFriction(friction);
        this.dirtyProperties = true;
    }

    @Override
    public void setRestitution(float restitution) {
        super.setRestitution(restitution);
        this.dirtyProperties = true;
    }

    @Override
    public void setTerrainLoadingEnabled(boolean doTerrainLoading) {
        super.setTerrainLoadingEnabled(doTerrainLoading);
        this.dirtyProperties = true;
    }
}