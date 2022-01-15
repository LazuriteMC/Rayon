package dev.lazurite.rayon.impl.bullet.collision.body;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;

public abstract class MinecraftRigidBody extends PhysicsRigidBody {
    protected final MinecraftSpace space;

    public MinecraftRigidBody(MinecraftSpace space, MinecraftShape shape, float mass) {
        super((CollisionShape) shape, mass);
        this.space = space;
    }

    public MinecraftRigidBody(MinecraftSpace space, MinecraftShape shape) {
        this(space, shape, massForStatic);
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }

    public MinecraftShape getMinecraftShape() {
        return (MinecraftShape) super.getCollisionShape();
    }

    public abstract Vector3f getOutlineColor();
}