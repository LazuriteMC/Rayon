package dev.lazurite.rayon.impl.event;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.PressureGenerator;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.TerrainGenerator;
import dev.lazurite.rayon.impl.event.network.EntityNetworking;
import dev.lazurite.rayon.impl.bullet.collision.body.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.EntityCollisionGenerator;
import dev.lazurite.rayon.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.impl.bullet.collision.space.supplier.level.ServerLevelSupplier;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.thread.util.ClientUtil;
import dev.lazurite.toolbox.api.event.ServerEvents;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import dev.lazurite.toolbox.api.math.VectorHelper;
import dev.lazurite.toolbox.api.network.PacketRegistry;
import dev.lazurite.toolbox.api.util.PlayerUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class ServerEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    public static void register() {
        // Rayon Events
        PhysicsSpaceEvents.STEP.register(PressureGenerator::step);
        PhysicsSpaceEvents.STEP.register(TerrainGenerator::step);
        PhysicsSpaceEvents.ELEMENT_ADDED.register(ServerEventHandler::onElementAddedToSpace);

        // Server Events
        ServerEvents.Lifecycle.LOAD_SERVER.register(ServerEventHandler::onServerStart);
        ServerEvents.Lifecycle.UNLOAD_SERVER.register(ServerEventHandler::onServerStop);
        ServerEvents.Tick.END_SERVER_TICK.register(ServerEventHandler::onServerTick);

        // Level Events
        ServerEvents.Lifecycle.LOAD_LEVEL.register(ServerEventHandler::onLevelLoad);
        ServerEvents.Tick.START_LEVEL_TICK.register(ServerEventHandler::onStartLevelTick);
        ServerEvents.Tick.START_LEVEL_TICK.register(ServerEventHandler::onEntityStartLevelTick);

        // Entity Events
        ServerEvents.Entity.LOAD.register(ServerEventHandler::onEntityLoad);
        ServerEvents.Entity.START_TRACKING.register(ServerEventHandler::onStartTrackingEntity);
        ServerEvents.Entity.STOP_TRACKING.register(ServerEventHandler::onStopTrackingEntity);
    }

    public static void onServerStart(MinecraftServer server) {
        thread = new PhysicsThread(server, Thread.currentThread(), new ServerLevelSupplier(server), "Server Physics Thread");
    }

    public static void onServerStop(MinecraftServer server) {
        thread.destroy();
    }

    public static void onServerTick(MinecraftServer server) {
        if (thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    public static void onStartLevelTick(Level level) {
        if (!ClientUtil.isPaused()) {
            MinecraftSpace.get(level).step();
        }
    }

    public static void onLevelLoad(MinecraftServer server, ServerLevel level) {
        final var space = new MinecraftSpace(thread, level);
        ((SpaceStorage) level).setSpace(space);
        PhysicsSpaceEvents.INIT.invoke(space);
    }

    public static void onElementAddedToSpace(MinecraftSpace space, ElementRigidBody rigidBody) {
        if (rigidBody instanceof EntityRigidBody entityBody) {
            final var pos = entityBody.getElement().cast().position();
            final var box = entityBody.getElement().cast().getBoundingBox();
            entityBody.setPhysicsLocation(Convert.toBullet(pos.add(0, box.getYsize() / 2.0, 0)));
        }
    }

    public static void onEntityLoad(Entity entity) {
        if (entity instanceof EntityPhysicsElement element && !PlayerUtil.tracking(entity).isEmpty()) {
            final var space = MinecraftSpace.get(entity.level);
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    public static void onStartTrackingEntity(Entity entity, ServerPlayer player) {
        if (entity instanceof EntityPhysicsElement element) {
            final var space = MinecraftSpace.get(entity.level);
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    public static void onStopTrackingEntity(Entity entity, ServerPlayer player) {
        if (entity instanceof EntityPhysicsElement element && PlayerUtil.tracking(entity).isEmpty()) {
            final var space = MinecraftSpace.get(entity.level);
            space.getWorkerThread().execute(() -> space.removeCollisionObject(element.getRigidBody()));
        }
    }

    public static void onEntityStartLevelTick(Level level) {
        final var space = MinecraftSpace.get(level);
        EntityCollisionGenerator.step(space);

        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            if (rigidBody.isActive()) {
                /* Movement */
                if (rigidBody.isPositionDirty()) {
                    EntityNetworking.sendMovement(rigidBody);
                }

                /* Properties */
                if (rigidBody.arePropertiesDirty()) {
                    EntityNetworking.sendProperties(rigidBody);
                }
            }

            /* Set entity position */
            final var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
            rigidBody.getElement().cast().absMoveTo(location.x, location.y, location.z);
        }
    }

    public static void onMovementPacketReceived(PacketRegistry.ServerboundContext context) {
        final var buf = context.byteBuf();
        final var entityId = buf.readInt();
        final var rotation = Convert.toBullet(QuaternionHelper.fromBuffer(buf));
        final var location = Convert.toBullet(VectorHelper.fromBuffer(buf));
        final var linearVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        final var angularVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        final var player = context.player();
        final var level = player.level;

        if (level != null) {
            final var entity = level.getEntity(entityId);

            if (entity instanceof EntityPhysicsElement element) {
                final var rigidBody = element.getRigidBody();

                if (rigidBody.getPriorityPlayer().equals(player)) {
                    PhysicsThread.get(level).execute(() -> {
                        rigidBody.setPhysicsRotation(rotation);
                        rigidBody.setPhysicsLocation(location);
                        rigidBody.setLinearVelocity(linearVelocity);
                        rigidBody.setAngularVelocity(angularVelocity);
                        rigidBody.activate();
                    });
                }
            }
        }
    }
}