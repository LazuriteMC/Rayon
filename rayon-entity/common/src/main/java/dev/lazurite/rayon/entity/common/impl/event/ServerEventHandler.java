package dev.lazurite.rayon.entity.common.impl.event;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.entity.common.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.common.impl.RayonEntity;
import dev.lazurite.rayon.entity.common.impl.collision.body.EntityRigidBody;
import dev.lazurite.rayon.entity.common.impl.collision.space.generator.EntityCollisionGenerator;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class ServerEventHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(RayonEntity.MOVEMENT_PACKET, ServerEventHandler::onMovement);
        PhysicsSpaceEvents.ELEMENT_ADDED.register(ServerEventHandler::onAddedToSpace);
        ServerEntityEvents.ENTITY_LOAD.register(ServerEventHandler::onEntityLoad);
        EntityTrackingEvents.START_TRACKING.register(ServerEventHandler::onStartTracking);
        EntityTrackingEvents.STOP_TRACKING.register(ServerEventHandler::onStopTracking);
        ServerTickEvents.START_WORLD_TICK.register(ServerEventHandler::onStartLevelTick);
    }

    private static void onAddedToSpace(MinecraftSpace space, ElementRigidBody rigidBody) {
        if (rigidBody instanceof EntityRigidBody entityBody) {
            final var pos = entityBody.getElement().asEntity().position();
            final var box = entityBody.getElement().asEntity().getBoundingBox();
            entityBody.setPhysicsLocation(Convert.toBullet(pos.add(0, box.getYsize() / 2.0, 0)));
        }
    }

    private static void onEntityLoad(Entity entity, ServerLevel level) {
        if (entity instanceof EntityPhysicsElement element && !PlayerLookup.tracking(entity).isEmpty()) {
            final var space = MinecraftSpace.get(entity.getLevel());
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    private static void onStartTracking(Entity entity, ServerPlayer player) {
        if (entity instanceof EntityPhysicsElement element) {
            final var space = MinecraftSpace.get(entity.getLevel());
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    private static void onStopTracking(Entity entity, ServerPlayer player) {
        if (entity instanceof EntityPhysicsElement element && PlayerLookup.tracking(entity).isEmpty()) {
            final var space = MinecraftSpace.get(entity.getLevel());
            space.getWorkerThread().execute(() -> space.removeCollisionObject(element.getRigidBody()));
        }
    }

    private static void onStartLevelTick(ServerLevel level) {
        final var space = MinecraftSpace.get(level);
        EntityCollisionGenerator.applyEntityCollisions(space);

        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            if (rigidBody.isActive()) {
                /* Movement */
                if (rigidBody.isPositionDirty() && rigidBody.getPriorityPlayer() == null) {
                    rigidBody.sendMovementPacket();
                }

                /* Properties */
                if (rigidBody.arePropertiesDirty()) {
                    rigidBody.sendPropertiesPacket();
                }
            }

            /* Set entity position */
            final var entity = rigidBody.getElement().asEntity();
            final var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
            final var offset = rigidBody.boundingBox(new BoundingBox()).getYExtent();
            entity.absMoveTo(location.x, location.y - offset, location.z);
        }
    }

    private static void onMovement(MinecraftServer server, ServerPlayer player, ServerGamePacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        final var entityId = buf.readInt();
        final var rotation = Convert.toBullet(QuaternionHelper.fromBuffer(buf));
        final var location = Convert.toBullet(VectorHelper.fromBuffer(buf));
        final var linearVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        final var angularVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));

        PhysicsThread.getOptional(server).ifPresent(thread -> thread.execute(() -> {
            final var level = player.getLevel();
            final var entity = level.getEntity(entityId);

            if (entity instanceof EntityPhysicsElement element) {
                final var rigidBody = element.getRigidBody();

                if (player.equals(rigidBody.getPriorityPlayer())) {
                    rigidBody.setPhysicsRotation(rotation);
                    rigidBody.setPhysicsLocation(location);
                    rigidBody.setLinearVelocity(linearVelocity);
                    rigidBody.setAngularVelocity(angularVelocity);

                    rigidBody.activate();
                    rigidBody.sendMovementPacket();
                }
            }
        }));
    }
}