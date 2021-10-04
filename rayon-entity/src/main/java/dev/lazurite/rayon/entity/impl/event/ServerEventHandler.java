package dev.lazurite.rayon.entity.impl.event;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.RayonEntity;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import dev.lazurite.rayon.entity.impl.collision.space.generator.EntityCollisionGenerator;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class ServerEventHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(RayonEntity.MOVEMENT_PACKET, ServerEventHandler::onMovement);
        PhysicsSpaceEvents.ELEMENT_ADDED.register(ServerEventHandler::onAddedToSpace);
        ServerEntityEvents.ENTITY_LOAD.register(ServerEventHandler::onEntityLoad);
        EntityTrackingEvents.START_TRACKING.register(ServerEventHandler::onStartTracking);
        EntityTrackingEvents.STOP_TRACKING.register(ServerEventHandler::onStopTracking);
        ServerTickEvents.START_WORLD_TICK.register(ServerEventHandler::onStartWorldTick);
    }

    private static void onAddedToSpace(MinecraftSpace space, ElementRigidBody rigidBody) {
        if (rigidBody instanceof EntityRigidBody entityBody) {
            final var pos = entityBody.getElement().asEntity().getPos();
            final var box = entityBody.getElement().asEntity().getBoundingBox();
            entityBody.setPhysicsLocation(Convert.toBullet(pos.add(0, box.getYLength() / 2.0, 0)));
        }
    }

    private static void onEntityLoad(Entity entity, ServerWorld world) {
        if (entity instanceof EntityPhysicsElement element && !PlayerLookup.tracking(entity).isEmpty()) {
            final var space = MinecraftSpace.get(entity.getEntityWorld());
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    private static void onStartTracking(Entity entity, ServerPlayerEntity player) {
        if (entity instanceof EntityPhysicsElement element) {
            final var space = MinecraftSpace.get(entity.getEntityWorld());
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    private static void onStopTracking(Entity entity, ServerPlayerEntity player) {
        if (entity instanceof EntityPhysicsElement element && PlayerLookup.tracking(entity).isEmpty()) {
            final var space = MinecraftSpace.get(entity.getEntityWorld());
            space.getWorkerThread().execute(() -> space.removeCollisionObject(element.getRigidBody()));
        }
    }

    private static void onStartWorldTick(ServerWorld world) {
        final var space = MinecraftSpace.get(world);
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
            entity.updatePosition(location.x, location.y - offset, location.z);
        }
    }

    private static void onMovement(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final var entityId = buf.readInt();
        final var rotation = Convert.toBullet(QuaternionHelper.fromBuffer(buf));
        final var location = Convert.toBullet(VectorHelper.fromBuffer(buf));
        final var linearVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        final var angularVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));

        PhysicsThread.getOptional(server).ifPresent(thread -> thread.execute(() -> {
            final var world = player.getEntityWorld();
            final var entity = world.getEntityById(entityId);

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