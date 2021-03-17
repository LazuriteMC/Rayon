package dev.lazurite.rayon.core.impl;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.thread.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.thread.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.thread.supplier.ClientWorldSupplier;
import dev.lazurite.rayon.core.impl.thread.supplier.WorldSupplier;
import dev.lazurite.rayon.core.impl.thread.util.ThreadStorage;
import dev.lazurite.rayon.core.impl.util.compat.ImmersiveWorldSupplier;
import dev.lazurite.rayon.core.impl.util.event.BetterClientLifecycleEvents;
import dev.lazurite.rayon.core.impl.util.math.interpolate.Frame;
import dev.lazurite.rayon.core.impl.thread.space.util.SpaceStorage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.util.concurrent.atomic.AtomicReference;

public class RayonCoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        /* Thread Events */
        AtomicReference<PhysicsThread> thread = new AtomicReference<>();
        ClientTickEvents.END_CLIENT_TICK.register(client -> { if (thread.get() != null) thread.get().tick(); });
        BetterClientLifecycleEvents.DISCONNECT.register((client, world) -> thread.get().destroy());
        BetterClientLifecycleEvents.GAME_JOIN.register((client, world, player) -> {
            WorldSupplier supplier;

            if (FabricLoader.getInstance().isModLoaded("immersive_portals")) {
                supplier = new ImmersiveWorldSupplier(client);
            } else {
                supplier = new ClientWorldSupplier(client);
            }

            thread.set(new PhysicsThread(client, supplier, "Client Physics Thread"));
            ((ThreadStorage) client).setPhysicsThread(thread.get());
        });

        /* World Events */
        BetterClientLifecycleEvents.LOAD_WORLD.register((client, world) -> {
            PhysicsSpaceEvents.PREINIT.invoker().onPreInit(thread.get(), world);
            ((SpaceStorage) world).putSpace(MinecraftSpace.MAIN, new MinecraftSpace(thread.get(), world));
        });

        ClientTickEvents.END_WORLD_TICK.register(world ->
            MinecraftSpace.get(world).getRigidBodiesByClass(ElementRigidBody.class).forEach(body -> {
                Frame prevFrame = body.getFrame();

                if (prevFrame == null) {
                    body.setFrame(new Frame(
                            body.getPhysicsLocation(new Vector3f()),
                            body.getPhysicsRotation(new Quaternion())));
                } else {
                    body.setFrame(new Frame(
                            prevFrame,
                            body.getPhysicsLocation(new Vector3f()),
                            body.getPhysicsRotation(new Quaternion())));
                }
            }));
    }
}
