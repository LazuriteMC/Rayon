package dev.lazurite.rayon.core.impl.event;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.level.ClientLevelSupplier;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.thread.util.ClientUtil;
import dev.lazurite.rayon.core.impl.util.debug.CollisionObjectDebugger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
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
        final var space = MinecraftSpace.get(level);

        if (!ClientUtil.isPaused()) {
            space.step();
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

    public static void onDebugRender(Level level, float tickDelta) {
        if (CollisionObjectDebugger.getInstance().isEnabled()) {
            CollisionObjectDebugger.getInstance().renderSpace(MinecraftSpace.get(level), tickDelta);
        }
    }
}