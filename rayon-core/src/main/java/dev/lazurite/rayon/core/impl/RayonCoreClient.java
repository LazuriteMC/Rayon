package dev.lazurite.rayon.core.impl;

import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.physics.debug.CollisionObjectDebugger;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.util.supplier.world.ClientWorldSupplier;
import dev.lazurite.rayon.core.impl.util.storage.ThreadStorage;
import dev.lazurite.rayon.core.impl.util.supplier.world.compat.ImmersiveWorldSupplier;
import dev.lazurite.rayon.core.impl.util.event.BetterClientLifecycleEvents;
import dev.lazurite.rayon.core.impl.util.storage.SpaceStorage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The client entrypoint for Rayon Core. Handles the lifecycle of the physics
 * thread as well as the creation of {@link MinecraftSpace}s.
 * @see RayonCore
 */
@Environment(EnvType.CLIENT)
public class RayonCoreClient implements ClientModInitializer {
    private static boolean serverHasRayon;

    @Override
    public void onInitializeClient() {
        var thread = new AtomicReference<PhysicsThread>();
        BetterClientLifecycleEvents.DISCONNECT.register((client, world) -> thread.get().destroy());

        BetterClientLifecycleEvents.GAME_JOIN.register((client, world, player) -> {
            var supplier = RayonCore.isImmersivePortalsPresent() ? new ImmersiveWorldSupplier(client) : new ClientWorldSupplier(client);
            thread.set(new PhysicsThread(client, Thread.currentThread(), supplier, "Client Physics Thread"));
            ((ThreadStorage) client).setPhysicsThread(thread.get());
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (thread.get() != null && thread.get().throwable != null) {
                throw new RuntimeException(thread.get().throwable);
            }
        });

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            var space = MinecraftSpace.get(world);
            space.step(space::canStep);
        });

        BetterClientLifecycleEvents.LOAD_WORLD.register((client, world) -> {
            PhysicsSpaceEvents.PREINIT.invoker().onPreInit(thread.get(), world);
            ((SpaceStorage) world).setSpace(new MinecraftSpace(thread.get(), world));
            PhysicsSpaceEvents.INIT.invoker().onInit(thread.get(), MinecraftSpace.get(world));
        });

        ClientPlayNetworking.registerGlobalReceiver(RayonCore.MODDED_SERVER, (client, handler, buf, sender) -> serverHasRayon = true);

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (CollisionObjectDebugger.getInstance().isEnabled()) {
                CollisionObjectDebugger.getInstance().render(context.world(), context.matrixStack(), context.tickDelta());
            }
        });
    }

    public static boolean isServerUsingRayon() {
        return serverHasRayon;
    }
}
