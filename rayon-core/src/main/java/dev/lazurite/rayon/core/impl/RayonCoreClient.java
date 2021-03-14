package dev.lazurite.rayon.core.impl;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.util.event.BetterClientLifecycleEvents;
import dev.lazurite.rayon.core.impl.util.math.interpolate.Frame;
import dev.lazurite.rayon.core.impl.util.space.SpaceStorage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.concurrent.atomic.AtomicReference;

public class RayonCoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        /* Thread Events */
        AtomicReference<PhysicsThread> thread = new AtomicReference<>();
        BetterClientLifecycleEvents.GAME_JOIN.register((client, world, player) -> thread.set(new PhysicsThread(client, "Client Physics Thread")));
        BetterClientLifecycleEvents.DISCONNECT.register((client, world) -> thread.get().destroy());
        ClientTickEvents.END_CLIENT_TICK.register(client -> { if (thread.get() != null) thread.get().tick(); });

        /* World Events */
        BetterClientLifecycleEvents.LOAD_WORLD.register((client, world) -> ((SpaceStorage) world).setSpace(new MinecraftSpace(thread.get(), world)));
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
