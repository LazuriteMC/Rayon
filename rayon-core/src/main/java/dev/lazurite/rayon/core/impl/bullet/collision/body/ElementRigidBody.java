package dev.lazurite.rayon.core.impl.bullet.collision.body;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.math.Converter;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Collection;

public abstract class ElementRigidBody extends MinecraftRigidBody {
    private final PhysicsElement element;

    public ElementRigidBody(PhysicsElement element, MinecraftSpace space, MinecraftShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(space, shape, mass, dragCoefficient, friction, restitution);
        this.element = element;
    }

    public PhysicsElement getElement() {
        return this.element;
    }

    public Collection<? extends PlayerEntity> getPlayersAround() {
        if (getSpace().isServer()) {
            var location = VectorHelper.toVec3d(Converter.toMinecraft(getPhysicsLocation(new Vector3f())));
            var world = (ServerWorld) getSpace().getWorld();
            var viewDistance = world.getServer().getPlayerManager().getViewDistance();
            return PlayerLookup.around(world, location, viewDistance);
        } else {
            return getSpace().getWorld().getPlayers();
        }
    }
}
