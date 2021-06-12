package dev.lazurite.rayon.entity.impl.event;

import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.bullet.math.VectorHelper;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.RayonEntity;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

@Environment(EnvType.CLIENT)
public class ClientEventHandler {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(RayonEntity.MOVEMENT_PACKET, ClientEventHandler::onMovement);
        ClientPlayNetworking.registerGlobalReceiver(RayonEntity.PROPERTIES_PACKET, ClientEventHandler::onProperties);
        ClientEntityEvents.ENTITY_LOAD.register(ClientEventHandler::onLoad);
        ClientEntityEvents.ENTITY_UNLOAD.register(ClientEventHandler::onUnload);
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

    private static void onMovement(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var entityId= buf.readInt();
        var rotation = QuaternionHelper.fromBuffer(buf);
        var location = VectorHelper.fromBuffer(buf);
        var linearVelocity = VectorHelper.fromBuffer(buf);
        var angularVelocity = VectorHelper.fromBuffer(buf);

        PhysicsThread.getOptional(client).ifPresent(thread -> thread.execute(() -> {
            if (client.player != null) {
                var world = client.player.getEntityWorld();
                var entity = world.getEntityById(entityId);

                if (entity instanceof EntityPhysicsElement element) {
                    var rigidBody = element.getRigidBody();
                    rigidBody.setPhysicsRotation(rotation);
                    rigidBody.setPhysicsLocation(location);
                    rigidBody.setLinearVelocity(linearVelocity);
                    rigidBody.setAngularVelocity(angularVelocity);
                    rigidBody.activate();
                }
            }
        }));
    }

    private static void onProperties(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var entityId = buf.readInt();
        var mass = buf.readFloat();
        var dragCoefficient = buf.readFloat();
        var friction = buf.readFloat();
        var restitution = buf.readFloat();
        var doTerrainLoading = buf.readBoolean();
        var priorityPlayer = buf.readUuid();

        PhysicsThread.getOptional(client).ifPresent(thread -> thread.execute(() -> {
            if (client.player != null) {
                var world = client.player.getEntityWorld();
                var entity = world.getEntityById(entityId);

                if (entity instanceof EntityPhysicsElement element) {
                    var rigidBody = element.getRigidBody();
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
