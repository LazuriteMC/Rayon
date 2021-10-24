package dev.lazurite.rayon.entity.impl.event;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvent;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.RayonEntity;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import dev.lazurite.rayon.entity.impl.collision.space.generator.EntityCollisionGenerator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RayonEntity.MODID)
public class ServerEventHandler {

    @SubscribeEvent
    public static void onAddedToSpace(PhysicsSpaceEvent.ElementAdded event) {
        if (event.getRigidBody() instanceof EntityRigidBody entityBody) {
            Entity entity = entityBody.getElement().asEntity();
            if(entity.level.isClientSide)return;
            final var pos = entity.position();
            final var box = entity.getBoundingBox();
            entityBody.setPhysicsLocation(Convert.toBullet(pos.add(0, box.getYsize() / 2.0, 0)));
            entityBody.sendMovementPacket();
        }
    }


    @SubscribeEvent
    public static void onEntityLoad(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if(entity.level.isClientSide)return;
        if (entity instanceof EntityPhysicsElement element && !entity.level.players().isEmpty()) {
            final var space = MinecraftSpace.get(entity.level);
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Entity entity = event.getTarget();
        if(entity.level.isClientSide)return;
        if (entity instanceof EntityPhysicsElement element) {
            final var space = MinecraftSpace.get(entity.level);
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    @SubscribeEvent
    public static void onStopTracking(PlayerEvent.StopTracking event) {
        Entity entity = event.getTarget();
        if(entity.level.isClientSide)return;
        if (entity instanceof EntityPhysicsElement element && RayonEntity.getPlayerTrackingEntity(entity).isEmpty()) {
            final var space = MinecraftSpace.get(entity.level);
            space.getWorkerThread().execute(() -> space.removeCollisionObject(element.getRigidBody()));
        }
    }

    @SubscribeEvent
    public static void onStartLevelTick(TickEvent.WorldTickEvent event) {
        if(event.phase != TickEvent.Phase.START)return;
        Level level = event.world;
        if(level.isClientSide)return;
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

    /*private static void onMovement(MinecraftServer server, ServerPlayer player, ServerGamePacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
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
    }*/
}