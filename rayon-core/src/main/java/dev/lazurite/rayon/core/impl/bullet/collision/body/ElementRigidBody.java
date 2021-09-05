package dev.lazurite.rayon.core.impl.bullet.collision.body;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.core.impl.util.debug.Debuggable;
import dev.lazurite.rayon.core.impl.bullet.collision.space.generator.util.Clump;
import dev.lazurite.rayon.core.impl.util.Frame;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Collection;

public abstract class ElementRigidBody extends PhysicsRigidBody implements Debuggable {
    protected final PhysicsElement element;
    protected final MinecraftSpace space;

    private float dragCoefficient;
    private final Frame frame;
    private Clump clump;
    private boolean doTerrainLoading;

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

    public Clump getClump() {
        return this.clump;
    }

    public void setClump(Clump clump) {
        this.clump = clump;
    }

    public void updateFrame() {
        getFrame().from(getFrame(), getPhysicsLocation(new Vector3f()), getPhysicsRotation(new Quaternion()));
    }


    @Override
    public MinecraftShape getCollisionShape() {
        return (MinecraftShape) super.getCollisionShape();
    }
}
