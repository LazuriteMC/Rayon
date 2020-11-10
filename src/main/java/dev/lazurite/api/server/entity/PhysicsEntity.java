package dev.lazurite.api.server.entity;

import dev.lazurite.api.LazuriteAPI;
import dev.lazurite.api.client.physics.handler.ClientPhysicsHandler;
import dev.lazurite.api.client.physics.handler.PhysicsHandler;
import dev.lazurite.api.client.physics.handler.ServerPhysicsHandler;
import dev.lazurite.api.client.physics.helper.AirHelper;
import dev.lazurite.api.network.packet.PhysicsHandlerC2S;
import dev.lazurite.api.network.packet.PhysicsHandlerS2C;
import dev.lazurite.api.util.math.QuaternionHelper;
import dev.lazurite.api.network.tracker.Config;
import dev.lazurite.api.network.tracker.EntityTrackerRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;

public abstract class PhysicsEntity extends NetworkSyncedEntity {
    public static final EntityTrackerRegistry.Entry<Integer> PLAYER_ID = EntityTrackerRegistry.register(new Config.Key<>("playerId", LazuriteAPI.INTEGER_TYPE), -1, PhysicsEntity.class, (entity, value) -> ((PhysicsEntity) entity).setPlayerID(value));
    public static final EntityTrackerRegistry.Entry<Integer> SIZE = EntityTrackerRegistry.register(new Config.Key<>("size", LazuriteAPI.INTEGER_TYPE), 2, PhysicsEntity.class);
    public static final EntityTrackerRegistry.Entry<Float> MASS = EntityTrackerRegistry.register(new Config.Key<>("mass", LazuriteAPI.FLOAT_TYPE), 10.0f, PhysicsEntity.class);
    public static final EntityTrackerRegistry.Entry<Float> DRAG_COEFFICIENT = EntityTrackerRegistry.register(new Config.Key<>("dragCoefficient", LazuriteAPI.FLOAT_TYPE), 0.5F, PhysicsEntity.class);
    public static final EntityTrackerRegistry.Entry<Boolean> NO_CLIP = EntityTrackerRegistry.register(new Config.Key<>("noClip", LazuriteAPI.BOOLEAN_TYPE), false, PhysicsEntity.class);

    protected PhysicsHandler physics;

    public PhysicsEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = false;
        this.physics = createPhysicsHandler(this);
    }

    public static PhysicsHandler createPhysicsHandler(PhysicsEntity entity) {
        if (entity.getEntityWorld().isClient()) {
            return new ClientPhysicsHandler(entity);
        } else {
            return new ServerPhysicsHandler(entity);
        }
    }

    @Override
    public void tick() {
        super.tick();
        updatePosition();
        updateEulerRotations();

        if (world.isClient()) {
            ClientPhysicsHandler physics = (ClientPhysicsHandler) this.physics;
            physics.setMass(getValue(MASS));
            physics.setSize(getValue(SIZE));
        }

        // kil
        if (!world.isClient()) {
            if (getValue(PLAYER_ID) != -1 && getEntityWorld().getEntityById(getValue(PLAYER_ID)) == null) {
                kill();
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void step(float delta) {
        if (age > 2) {
            ((ClientPhysicsHandler) getPhysics()).applyForce(AirHelper.getResistanceForce(
                    physics.getLinearVelocity(),
                    getValue(SIZE),
                    getValue(DRAG_COEFFICIENT)
            ));
        }
    }

    public void updatePositionAndAngles(Vector3f position, float yaw, float pitch) {
        this.updatePositionAndAngles(position.x, position.y, position.z, yaw, pitch);
        physics.setPosition(position);
        setYaw(yaw);
    }

    @Override
    protected void sendNetworkPacket() {
        if (getEntityWorld().isClient()) {
            ClientPhysicsHandler physics = (ClientPhysicsHandler) getPhysics();

            if (physics.isActive()) {
                PhysicsHandlerC2S.send(physics);
            } else {
                physics.setPrevOrientation(physics.getOrientation());
                physics.setOrientation(physics.getNetOrientation());
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
        PlayerEntity player = (PlayerEntity) getEntityWorld().getEntityById(playerID);
        if (player != null) {
            setCustomName(new LiteralText(player.getGameProfile().getName()));
            setCustomNameVisible(true);
        }
    }

    protected void updatePosition() {
        updatePosition(
                getPhysics().getPosition().x,
                getPhysics().getPosition().y,
                getPhysics().getPosition().z
        );
    }

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
