package dev.lazurite.rayon.core.impl.bullet.collision.body;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.core.impl.util.debug.Debuggable;
import dev.lazurite.rayon.core.impl.util.Frame;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ElementRigidBody extends PhysicsRigidBody implements Debuggable {
    protected final PhysicsElement element;
    protected final MinecraftSpace space;

    private final Frame frame;
    private boolean doTerrainLoading;
    private float dragCoefficient;
    private Map<BlockPos, TerrainObject> terrainObjects = new HashMap<>();

    public ElementRigidBody(PhysicsElement element, MinecraftSpace space, MinecraftShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(shape, mass);
        this.space = space;
        this.element = element;
        this.frame = new Frame();

        this.setDoTerrainLoading(!this.isStatic());
        this.setDragCoefficient(dragCoefficient);
        this.setFriction(friction);
        this.setRestitution(restitution);

    }

    public PhysicsElement getElement() {
        return this.element;
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

    public Frame getFrame() {
        return this.frame;
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }

    public Collection<? extends PlayerEntity> getPlayersAround() {
        if (getSpace().isServer()) {
            var location = VectorHelper.toVec3d(Convert.toMinecraft(getPhysicsLocation(new Vector3f())));
            var world = (ServerWorld) getSpace().getWorld();
            var viewDistance = world.getServer().getPlayerManager().getViewDistance();
            return PlayerLookup.around(world, location, viewDistance);
        } else {
            return getSpace().getWorld().getPlayers();
        }
    }

    @Override
    public Vector3f getOutlineColor() {
        return new Vector3f(1.0f, 1.0f, 1.0f);
    }

    @Override
    public float getOutlineAlpha() {
        return 1.0f;
    }

    public Map<BlockPos, TerrainObject> getTerrainObjects() {
        return this.terrainObjects;
    }

    public void setTerrainObjects(Map<BlockPos, TerrainObject> terrainObjects) {
        this.terrainObjects = terrainObjects;
    }

    public void updateFrame() {
        getFrame().from(getFrame(), getPhysicsLocation(new Vector3f()), getPhysicsRotation(new Quaternion()));
    }

    @Override
    public MinecraftShape getCollisionShape() {
        return (MinecraftShape) super.getCollisionShape();
    }

    /**
     * Assumes the rigid body is sphere shaped for simplicity.
     * @param density the density of the surrounding fluid (air, water, etc. - not the rigid body)
     */
    public void applyDragForce(float density) {
        final float dragCoefficient = getDragCoefficient();
        final float area = boundingBox(new BoundingBox()).getExtent(new Vector3f()).lengthSquared();
        final float k = density * dragCoefficient * area * 0.5f;
        final float v2 = -getLinearVelocity(new Vector3f()).lengthSquared();
        final var direction = getLinearVelocity(new Vector3f()).normalize();
        final var force = new Vector3f().set(direction).multLocal(v2).multLocal(k);

        if (Float.isFinite(force.lengthSquared()) && force.lengthSquared() > 0.1f) {
            applyCentralForce(force);
        }
    }
}
