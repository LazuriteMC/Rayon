package dev.lazurite.rayon.entity.impl.event;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.player.ClientPlayerSupplier;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.RayonEntity;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import dev.lazurite.rayon.entity.impl.collision.space.generator.EntityCollisionGenerator;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class ClientEventHandler {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(RayonEntity.MOVEMENT_PACKET, ClientEventHandler::onMovement);
        ClientPlayNetworking.registerGlobalReceiver(RayonEntity.PROPERTIES_PACKET, ClientEventHandler::onProperties);
        ClientEntityEvents.ENTITY_LOAD.register(ClientEventHandler::onLoad);
        ClientEntityEvents.ENTITY_UNLOAD.register(ClientEventHandler::onUnload);
        ClientTickEvents.START_WORLD_TICK.register(ClientEventHandler::onStartWorldTick);
    }

    private static void onLoad(Entity entity, ClientWorld world) {
        if (entity instanceof EntityPhysicsElement element) {
            PhysicsThread.get(world).execute(() ->
                    MinecraftSpace.getOptional(world).ifPresent(space ->
                            space.addCollisionObject(element.getRigidBody())
                    )
            );
        }
    }

    private static void onUnload(Entity entity, ClientWorld world) {
        if (entity instanceof EntityPhysicsElement element) {
            PhysicsThread.get(world).execute(() ->
                MinecraftSpace.getOptional(world).ifPresent(space ->
                        space.removeCollisionObject(element.getRigidBody())
                )
            );
       }
    }

    private static void onStartWorldTick(ClientWorld world) {
        final var space = MinecraftSpace.get(world);
        EntityCollisionGenerator.applyEntityCollisions(space);

        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            /* Movement */
            if (rigidBody.isActive() && rigidBody.isPositionDirty() && ClientPlayerSupplier.get().equals(rigidBody.getPriorityPlayer())) {
                rigidBody.sendMovementPacket();
            }

            /* Set entity position */
            final var element = ((EntityPhysicsElement) rigidBody.getElement());
            final var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
            final var offset = rigidBody.boundingBox(new BoundingBox()).getYExtent();
            element.asEntity().updatePosition(location.x, location.y - offset, location.z);
        }
    }


    private static void onMovement(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final var entityId= buf.readInt();
        final var rotation = QuaternionHelper.fromBuffer(buf);
        final var location = VectorHelper.fromBuffer(buf);
        final var linearVelocity = VectorHelper.fromBuffer(buf);
        final var angularVelocity = VectorHelper.fromBuffer(buf);

        PhysicsThread.getOptional(client).ifPresent(thread -> thread.execute(() -> {
            if (client.player != null) {
                final var world = client.player.getEntityWorld();
                final var entity = world.getEntityById(entityId);

                if (entity instanceof EntityPhysicsElement element) {
                    final var rigidBody = element.getRigidBody();
                    rigidBody.setPhysicsRotation(Convert.toBullet(rotation));
                    rigidBody.setPhysicsLocation(Convert.toBullet(location));
                    rigidBody.setLinearVelocity(Convert.toBullet(linearVelocity));
                    rigidBody.setAngularVelocity(Convert.toBullet(angularVelocity));
                    rigidBody.activate();
                }
            }
        }));
    }

    private static void onProperties(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final var entityId = buf.readInt();
        final var mass = buf.readFloat();
        final var dragCoefficient = buf.readFloat();
        final var friction = buf.readFloat();
        final var restitution = buf.readFloat();
        final var doTerrainLoading = buf.readBoolean();
        final var priorityPlayer = buf.readUuid();

        PhysicsThread.getOptional(client).ifPresent(thread -> thread.execute(() -> {
            if (client.player != null) {
                final var world = client.player.getEntityWorld();
                final var entity = world.getEntityById(entityId);

                if (entity instanceof EntityPhysicsElement element) {
                    final var rigidBody = element.getRigidBody();
                    rigidBody.setMass(mass);
                    rigidBody.setDragCoefficient(dragCoefficient);
                    rigidBody.setFriction(friction);
                    rigidBody.setRestitution(restitution);
                    rigidBody.setDoTerrainLoading(doTerrainLoading);
                    rigidBody.prioritize(rigidBody.getSpace().getWorld().getPlayerByUuid(priorityPlayer));
                }
            }
        }));
    }
}
