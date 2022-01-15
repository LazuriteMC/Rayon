package dev.lazurite.rayon.impl.event;

import com.jme3.math.Vector3f;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityNetworking;
import dev.lazurite.rayon.impl.bullet.collision.body.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.EntityCollisionGenerator;
import dev.lazurite.rayon.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.impl.bullet.collision.space.supplier.level.ServerLevelSupplier;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.thread.util.ClientUtil;
import dev.lazurite.toolbox.api.util.PlayerUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class ServerEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    @ExpectPlatform
    public static void register() {
        throw new AssertionError();
    }

    public static void onServerStart(MinecraftServer server) {
        thread = new PhysicsThread(server, Thread.currentThread(), new ServerLevelSupplier(server), "Server Physics Thread");
    }

    public static void onServerStop(MinecraftServer server) {
        thread.destroy();
    }

    public static void onServerTick() {
        if (thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    public static void onStartLevelTick(Level level) {
        if (!ClientUtil.isPaused()) {
            MinecraftSpace.get(level).step();
        }
    }

    public static void onLevelLoad(Level level) {
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

    public static void onEntityLoad(Entity entity, Level level) {
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
}