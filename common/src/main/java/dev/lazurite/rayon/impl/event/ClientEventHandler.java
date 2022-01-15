package dev.lazurite.rayon.impl.event;

import com.jme3.math.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityNetworking;
import dev.lazurite.rayon.impl.bullet.collision.body.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.EntityCollisionGenerator;
import dev.lazurite.rayon.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.impl.bullet.collision.space.supplier.level.ClientLevelSupplier;
import dev.lazurite.rayon.impl.bullet.collision.space.supplier.player.ClientPlayerSupplier;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.thread.util.ClientUtil;
import dev.lazurite.rayon.impl.util.debug.CollisionObjectDebugger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class ClientEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    @ExpectPlatform
    public static void register() {
        throw new AssertionError();
    }

    public static void onStartLevelTick(Level level) {
        if (!ClientUtil.isPaused()) {
            MinecraftSpace.get(level).step();
        }
    }

    public static void onLevelLoad(Minecraft minecraft, ClientLevel level) {
        final var space = new MinecraftSpace(thread, level);
        ((SpaceStorage) level).setSpace(space);
        PhysicsSpaceEvents.INIT.invoke(space);
    }

    public static void onClientTick(Minecraft minecraft) {
        if (thread != null && thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    public static void onGameJoin(Minecraft minecraft, ClientLevel level, LocalPlayer player) {
//        var supplier = RayonCore.isImmersivePortalsPresent() ? new ImmersiveWorldSupplier(minecraft) : new ClientLevelSupplier(minecraft);
        final var supplier = new ClientLevelSupplier(minecraft);
        thread = new PhysicsThread(minecraft, Thread.currentThread(), supplier, "Client Physics Thread");
    }

    public static void onDisconnect(Minecraft minecraft, ClientLevel level) {
        thread.destroy();
    }

    public static void onDebugRender(Level level, PoseStack stack, float tickDelta) {
        if (CollisionObjectDebugger.isEnabled()) {
            CollisionObjectDebugger.renderSpace(MinecraftSpace.get(level), stack, tickDelta);
        }
    }

    public static void onEntityLoad(Entity entity, Level level) {
        if (entity instanceof EntityPhysicsElement element) {
            PhysicsThread.get(level).execute(() ->
                    MinecraftSpace.getOptional(level).ifPresent(space ->
                            space.addCollisionObject(element.getRigidBody())
                    )
            );
        }
    }

    public static void onEntityUnload(Entity entity, Level level) {
        if (entity instanceof EntityPhysicsElement element) {
            PhysicsThread.get(level).execute(() ->
                    MinecraftSpace.getOptional(level).ifPresent(space ->
                            space.removeCollisionObject(element.getRigidBody())
                    )
            );
        }
    }

    public static void onEntityStartLevelTick(Level level) {
        final var space = MinecraftSpace.get(level);
        EntityCollisionGenerator.step(space);

        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            /* Movement */
            if (rigidBody.isActive() && rigidBody.isPositionDirty() && ClientPlayerSupplier.get().equals(rigidBody.getPriorityPlayer())) {
                EntityNetworking.sendMovement(rigidBody);
            }

            /* Set entity position */
            final var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
            rigidBody.getElement().cast().absMoveTo(location.x, location.y, location.z);
        }
    }
}