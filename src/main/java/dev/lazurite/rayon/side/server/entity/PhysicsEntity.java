package dev.lazurite.rayon.side.server.entity;

import dev.lazurite.rayon.physics.handler.ClientPhysicsHandler;
import dev.lazurite.rayon.physics.handler.PhysicsHandler;
import dev.lazurite.rayon.helper.ShapeHelper;
import dev.lazurite.rayon.network.packet.PhysicsHandlerC2S;
import dev.lazurite.rayon.network.packet.PhysicsHandlerS2C;
import dev.lazurite.rayon.thimble.tracker.EntityTrackerRegistry;
import dev.lazurite.rayon.thimble.type.GenericTypeRegistry;
import dev.lazurite.rayon.helper.QuaternionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;

import javax.vecmath.Vector3f;

/**
 * This class is the main class to be used by mod authors utilizing Rayon. Any entity you
 * want to have physics attributes and behave accordingly must extend from this class.
 * @author Ethan Johnson
 */
public abstract class PhysicsEntity extends NetworkSyncedEntity {
    public static final EntityTrackerRegistry.Entry<Boolean> NO_CLIP = EntityTrackerRegistry.register("noClip", GenericTypeRegistry.BOOLEAN_TYPE, false, PhysicsEntity.class, (entity, value) -> entity.noClip = value);


    /**
     * Updates the position and angles of the entity in Minecraft and also Rayon.
     * @param position the entity's position
     * @param yaw the entity's yaw angle
     * @param pitch the entity's pitch angle
     */
    public void updatePositionAndAngles(Vector3f position, float yaw, float pitch) {
        this.updatePositionAndAngles(position.x, position.y, position.z, yaw, pitch);
        physics.setPosition(position);
        setYaw(yaw);
    }

    @Override
    protected void sendNetworkPacket() {
        if (getEntityWorld().isClient()) {
            if (physics.isActive()) {
                PhysicsHandlerC2S.send(getPhysics());
            }
        } else {
            PhysicsHandlerS2C.send(getPhysics());
        }
    }

    /**
     * Sets the player ID used by the physics entity.
     * @param playerID the player ID
     */
    protected void setPlayerID(int playerID) {
        Entity entity = getEntityWorld().getEntityById(playerID);
        if (entity instanceof PlayerEntity) {
            setCustomName(new LiteralText(((PlayerEntity) entity).getGameProfile().getName()));
            setCustomNameVisible(true);
        }
    }

    public void markDirty() {
        setValue(DIRTY, true);
    }

    @Override
    public void setYaw(float yaw) {
        if (world.isClient()) {
            ((ClientPhysicsHandler) physics).rotateY(yaw);
        }

        this.prevYaw = this.yaw;
        this.yaw = yaw;
    }
}
