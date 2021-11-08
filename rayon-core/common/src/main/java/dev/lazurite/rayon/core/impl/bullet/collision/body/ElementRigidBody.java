package dev.lazurite.rayon.core.impl.bullet.collision.body;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.core.impl.util.Frame;
import dev.lazurite.rayon.core.impl.util.debug.Debuggable;
import com.jme3.math.Vector3f;
import dev.lazurite.toolbox.common.math.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.HashMap;
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

    public Collection<? extends Player> getPlayersAround() {
        if (getSpace().isServer()) {
            var location = VectorHelper.toVec3(Convert.toMinecraft(getPhysicsLocation(new Vector3f())));
            var world = (ServerLevel) getSpace().getLevel();
            var viewDistance = world.getServer().getPlayerList().getViewDistance();
            return null; // TODO !!!!!!!!!!!!! PlayerLookup.around(world, location, viewDistance);
        } else {
            return getSpace().getLevel().players();
        }
    }

    @Override
    public Vector3f getOutlineColor() {
        return this.isActive() ? new Vector3f(1.0f, 1.0f, 1.0f) : new Vector3f(1.0f, 0.0f, 0.0f);
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
     * @param density the density of the surrounding fluid (air, water, etc. - not the rigid body)
     */
    public void applyDragForce(float density) {
        final var area = (float) Math.pow(Convert.toMinecraft(
                this.getCollisionShape().boundingBox(this.getPhysicsLocation(null), new Quaternion(), null))
                .getSize(), 2);
        final var v2 = this.getLinearVelocity(null).lengthSquared();
        final var direction = getLinearVelocity(null).normalize();

        /*
            0.5CpAv2
            C = drag coefficient of object
            p = density of fluid
            A = area of object
            v2 = velocity squared of object
        */
        final var force = new Vector3f().set(direction).multLocal(-0.5f * this.getDragCoefficient() * density * area * v2);

        if (Float.isFinite(force.lengthSquared()) && force.lengthSquared() > 0.001f) {
            this.applyCentralForce(force);
        }
    }
}
