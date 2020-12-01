package dev.lazurite.rayon.server.entity;

import com.bulletphysics.collision.shapes.CollisionShape;
import dev.lazurite.rayon.client.handler.ClientPhysicsHandler;
import dev.lazurite.rayon.client.handler.PhysicsHandler;
import dev.lazurite.rayon.client.helper.ShapeHelper;
import dev.lazurite.rayon.network.packet.PhysicsHandlerC2S;
import dev.lazurite.rayon.network.packet.PhysicsHandlerS2C;
import dev.lazurite.rayon.network.tracker.EntityTrackerRegistry;
import dev.lazurite.rayon.network.tracker.generic.GenericTypeRegistry;
import dev.lazurite.rayon.client.helper.QuaternionHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;

/**
 * This class is the main class to be used by mod authors utilizing Rayon. Any entity you
 * want to have physics attributes and behave accordingly must extend from this class.
 * @author Ethan Johnson
 */
public abstract class PhysicsEntity extends NetworkSyncedEntity {
    public static final EntityTrackerRegistry.Entry<Integer> PLAYER_ID = EntityTrackerRegistry.register("playerId", GenericTypeRegistry.INTEGER_TYPE, -1, PhysicsEntity.class, (entity, value) -> ((PhysicsEntity) entity).setPlayerID(value));
    public static final EntityTrackerRegistry.Entry<Boolean> NO_CLIP = EntityTrackerRegistry.register("noClip", GenericTypeRegistry.BOOLEAN_TYPE, false, PhysicsEntity.class, (entity, value) -> entity.noClip = value);
    public static final EntityTrackerRegistry.Entry<Boolean> DIRTY = EntityTrackerRegistry.register("dirty", GenericTypeRegistry.BOOLEAN_TYPE, false, PhysicsEntity.class);
    public static final EntityTrackerRegistry.Entry<Float> DRAG_COEFFICIENT = EntityTrackerRegistry.register("dragCoefficient", GenericTypeRegistry.FLOAT_TYPE, 0.5F, PhysicsEntity.class);
    public static final EntityTrackerRegistry.Entry<Float> MASS = EntityTrackerRegistry.register("mass", GenericTypeRegistry.FLOAT_TYPE, 5.0f, PhysicsEntity.class, (entity, value) -> {
        if (entity.getEntityWorld().isClient()) {
            ((ClientPhysicsHandler) ((PhysicsEntity) entity).getPhysics()).setMass(value);
        }
    });

    protected PhysicsHandler physics;

    public PhysicsEntity(EntityType<?> type, World world) {
        this(type, world, ShapeHelper.getEntityShape(type));
    }

    public PhysicsEntity(EntityType<?> type, World world, CollisionShape shape) {
        super(type, world, 5);
        this.physics = PhysicsHandler.create(this, shape);
    }

    @Override
    public void tick() {
        super.tick();

        updateEulerRotations();
        updatePosition(
                getPhysics().getPosition().x,
                getPhysics().getPosition().y,
                getPhysics().getPosition().z
        );

        if (world.isClient()) {
            ClientPhysicsHandler physics = (ClientPhysicsHandler) this.physics;
            physics.updateNetOrientation();
        } else {
            if (getValue(PLAYER_ID) != -1 && getEntityWorld().getEntityById(getValue(PLAYER_ID)) == null) {
//                kill();
            }
        }
    }

    /**
     * This method is called every frame on the render thread. The purpose is
     * to make changes to the {@link PhysicsHandler} here, on the client. For example,
     * if you wish to apply a constant force, do it here, not in {@link PhysicsEntity#tick()}.
     * @param delta delta time
     */
    @Environment(EnvType.CLIENT)
    public void step(float delta) {
        ClientPhysicsHandler physics = (ClientPhysicsHandler) getPhysics();

//        if (age > 2) {
//            physics.applyForce(AirHelper.getResistanceForce(physics.getLinearVelocity(), getValue(DRAG_COEFFICIENT)));
//        }
    }

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

    /**
     * Changes the vanilla minecraft rotations
     * to match the physics orientation.
     */
    protected void updateEulerRotations() {
        prevYaw = yaw;
        yaw = QuaternionHelper.getYaw(physics.getOrientation());

        while(yaw - prevYaw < -180.0F) {
            prevYaw -= 360.0F;
        }

        while(yaw - prevYaw >= 180.0F) {
            prevYaw += 360.0F;
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

    @Override
    public boolean collides() {
        return true;
    }

    public PhysicsHandler getPhysics() {
        return physics;
    }
}
