package dev.lazurite.rayon.core.impl.event;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.level.ClientLevelSupplier;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.util.debug.CollisionObjectDebugger;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.toolbox.common.event.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

public final class ClientEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    public static void register() {
        // Client Events
        ClientLifecycleEvents.LOGIN.register(ClientEventHandler::onGameJoin);
        ClientLifecycleEvents.DISCONNECT.register(ClientEventHandler::onDisconnect);
        ClientTickEvent.CLIENT_POST.register(ClientEventHandler::onClientTick);

        // World Events
        ClientLifecycleEvents.LOAD_LEVEL.register(ClientEventHandler::onLevelLoad);
        ClientTickEvent.CLIENT_LEVEL_PRE.register(ClientEventHandler::onStartLevelTick);

        // Render Events
        ClientGuiEvent.RENDER_PRE.register(ClientEventHandler::onPreRender);
    }

    static void onStartLevelTick(ClientLevel level) {
        final var space = MinecraftSpace.get(level);

        if (!space.getWorkerThread().isPaused()) {
            space.step();
        }
    }

    static void onLevelLoad(Minecraft minecraft, ClientLevel level) {
        final var space = new MinecraftSpace(thread, level);
        ((SpaceStorage) level).setSpace(space);
        PhysicsSpaceEvents.INIT.invoker().onInit(space);
    }

    static void onClientTick(Minecraft minecraft) {
        if (thread != null && thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    static void onGameJoin(Minecraft minecraft, ClientLevel level, LocalPlayer player) {
//        var supplier = RayonCore.isImmersivePortalsPresent() ? new ImmersiveWorldSupplier(minecraft) : new ClientLevelSupplier(minecraft);
        final var supplier = new ClientLevelSupplier(minecraft);
        thread = new PhysicsThread(minecraft, Thread.currentThread(), supplier, "Client Physics Thread");
    }

    static void onDisconnect(Minecraft minecraft, ClientLevel level) {
        thread.destroy();
    }

    // This is prolly wrong lmao
    static EventResult onPreRender(Screen screen, PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (CollisionObjectDebugger.getInstance().isEnabled()) {
            CollisionObjectDebugger.getInstance().renderSpace(MinecraftSpace.get(Minecraft.getInstance().level), delta);
        }

        return EventResult.pass();
    }
}
