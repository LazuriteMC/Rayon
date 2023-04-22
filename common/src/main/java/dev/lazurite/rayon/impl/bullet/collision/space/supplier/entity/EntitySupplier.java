package dev.lazurite.rayon.impl.bullet.collision.space.supplier.entity;

import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;

import java.util.List;

public interface EntitySupplier {

    default List<Entity> getInsideOf(ElementRigidBody rigidBody, AABB box) {
        if (!rigidBody.isInWorld()) {
            return List.of();
        }

        return rigidBody.getSpace().getLevel().getEntitiesOfClass(Entity.class, box,
                entity ->
                        // Entity can be a Boat, Minecart, or any LivingEntity so long as it is not a player in spectator mode.
                        (
                            entity instanceof Boat ||
                            entity instanceof Minecart ||
                            (
                                entity instanceof LivingEntity &&
                                !(entity instanceof Player player && this.getGameType(player) == GameType.SPECTATOR)
                            )
                        )
                        && !EntityPhysicsElement.is(entity));
    }

    GameType getGameType(Player player);
}
