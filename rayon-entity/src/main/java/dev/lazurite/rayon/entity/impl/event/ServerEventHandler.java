package dev.lazurite.rayon.entity.impl.event;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.body.MinecraftRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.entity.EntitySupplier;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.player.ClientPlayerSupplier;
import dev.lazurite.rayon.core.impl.bullet.math.BoxHelper;
import dev.lazurite.rayon.core.impl.bullet.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.bullet.math.VectorHelper;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.RayonEntity;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
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
        PhysicsSpaceEvents.STEP.register(ServerEventHandler::onStep);
        PhysicsSpaceEvents.ELEMENT_ADDED.register(ServerEventHandler::onAddedToSpace);
        ServerEntityEvents.ENTITY_LOAD.register(ServerEventHandler::onEntityLoad);
        EntityTrackingEvents.START_TRACKING.register(ServerEventHandler::onStartTracking);
        EntityTrackingEvents.STOP_TRACKING.register(ServerEventHandler::onStopTracking);
    }

    private static void onAddedToSpace(MinecraftSpace space, ElementRigidBody rigidBody) {
        if (rigidBody instanceof EntityRigidBody entityBody) {
            var pos = entityBody.getElement().asEntity().getPos();
            var box = entityBody.getElement().asEntity().getBoundingBox();
            entityBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(pos.add(0, box.getYLength() / 2.0, 0)));
        }
    }

    private static void onEntityLoad(Entity entity, ServerWorld world) {
        if (entity instanceof EntityPhysicsElement element && !PlayerLookup.tracking(entity).isEmpty()) {
            var space = MinecraftSpace.get(entity.getEntityWorld());
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    private static void onStartTracking(Entity entity, ServerPlayerEntity player) {
        if (entity instanceof EntityPhysicsElement element) {
            var space = MinecraftSpace.get(entity.getEntityWorld());
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    private static void onStopTracking(Entity entity, ServerPlayerEntity player) {
        if (entity instanceof EntityPhysicsElement element && PlayerLookup.tracking(entity).isEmpty()) {
            var space = MinecraftSpace.get(entity.getEntityWorld());
            space.getWorkerThread().execute(() -> space.removeCollisionObject(element.getRigidBody()));
        }
    }

    private static void onStep(MinecraftSpace space) {
        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            /* Movement */
            if (rigidBody.isActive() && rigidBody.isPositionDirty()) {
                if ((space.isServer() && rigidBody.getPriorityPlayer() == null) || (!space.isServer() && ClientPlayerSupplier.get().equals(rigidBody.getPriorityPlayer()))) {
                    rigidBody.sendMovementPacket();
                }
            }

            /* Properties */
            if (space.isServer() && rigidBody.isActive() && rigidBody.arePropertiesDirty()) {
                rigidBody.sendPropertiesPacket();
            }

            /* Entity Collisions */
            {
                var box = rigidBody.boundingBox(new BoundingBox());
                var location = rigidBody.getPhysicsLocation(new Vector3f()).subtract(new Vector3f(0, -box.getYExtent(), 0));
                var mass = rigidBody.getMass();

                for (var entity : EntitySupplier.getInsideOf(rigidBody)) {
                    var entityPos = VectorHelper.vec3dToVector3f(entity.getPos().add(0, entity.getBoundingBox().getYLength(), 0));
                    var normal = location.subtract(entityPos).multLocal(new Vector3f(1, 0, 1)).normalize();

                    var intersection = entity.getBoundingBox().intersection(BoxHelper.bulletToMinecraft(box));
                    var force = normal.clone().multLocal((float) intersection.getAverageSideLength() / (float) BoxHelper.bulletToMinecraft(box).getAverageSideLength())
                            .multLocal(mass).multLocal(new Vector3f(1, 0, 1));
                    rigidBody.applyCentralImpulse(force);
                }
            }

            /* Set entity position */
            var element = ((EntityPhysicsElement) rigidBody.getElement());
            var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
            var offset = rigidBody.boundingBox(new BoundingBox()).getYExtent();
           element.asEntity().updatePosition(location.x, location.y - offset, location.z);
        }
    }

    private static void onMovement(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var entityId = buf.readInt();
        var rotation = QuaternionHelper.fromBuffer(buf);
        var location = VectorHelper.fromBuffer(buf);
        var linearVelocity = VectorHelper.fromBuffer(buf);
        var angularVelocity = VectorHelper.fromBuffer(buf);

        PhysicsThread.getOptional(server).ifPresent(thread -> thread.execute(() -> {
            var world = player.getEntityWorld();
            var entity = world.getEntityById(entityId);

            if (entity instanceof EntityPhysicsElement element) {
                var rigidBody = element.getRigidBody();

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