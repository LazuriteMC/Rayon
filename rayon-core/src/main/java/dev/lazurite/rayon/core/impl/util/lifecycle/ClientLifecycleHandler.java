package dev.lazurite.rayon.core.impl.util.lifecycle;

import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.util.SpaceStorage;
import dev.lazurite.rayon.core.impl.util.supplier.world.ClientWorldSupplier;
import dev.lazurite.rayon.core.impl.util.supplier.world.compat.ImmersiveWorldSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public final class ClientLifecycleHandler {
    private static PhysicsThread thread;

    public static void onWorldTick(ClientWorld world) {
        var space = MinecraftSpace.get(world);
        space.step(space::canStep);
    }

    public static void onWorldLoad(MinecraftClient client, ClientWorld world) {
        var space = new MinecraftSpace(thread, world);
        ((SpaceStorage) world).setSpace(space);
        PhysicsSpaceEvents.INIT.invoker().onInit(space);
    }

    public static void onClientTick(MinecraftClient client) {
        if (thread != null && thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    public static void onGameJoin(MinecraftClient client, ClientWorld world, ClientPlayerEntity player) {
        var supplier = RayonCore.isImmersivePortalsPresent() ? new ImmersiveWorldSupplier(client) : new ClientWorldSupplier(client);
        thread = new PhysicsThread(client, Thread.currentThread(), supplier, "Client Physics Thread");
    }

    public static void onDisconnect(MinecraftClient client, ClientWorld world) {
        thread.destroy();
    }

    public static PhysicsThread getThread() {
        return thread;
    }
}
