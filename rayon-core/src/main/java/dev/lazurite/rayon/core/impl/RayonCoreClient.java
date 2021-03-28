package dev.lazurite.rayon.core.impl;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.physics.util.supplier.ClientWorldSupplier;
import dev.lazurite.rayon.core.impl.physics.util.supplier.WorldSupplier;
import dev.lazurite.rayon.core.impl.physics.util.thread.ThreadStorage;
import dev.lazurite.rayon.core.impl.util.compat.ImmersiveWorldSupplier;
import dev.lazurite.rayon.core.impl.util.event.BetterClientLifecycleEvents;
import dev.lazurite.rayon.core.impl.physics.space.util.SpaceStorage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The client entrypoint for Rayon Core. Handles the lifecycle of the physics
 * thread as well as the creation of {@link MinecraftSpace}s.
 * @see RayonCoreCommon
 */
@Environment(EnvType.CLIENT)
public class RayonCoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        /* Thread Events */
        AtomicReference<PhysicsThread> thread = new AtomicReference<>();
        BetterClientLifecycleEvents.DISCONNECT.register((client, world) -> thread.get().destroy());

        BetterClientLifecycleEvents.GAME_JOIN.register((client, world, player) -> {
            WorldSupplier supplier = RayonCoreCommon.isImmersivePortalsPresent() ?
                    new ImmersiveWorldSupplier(client) : new ClientWorldSupplier(client);

            thread.set(new PhysicsThread(client, supplier, "Client Physics Thread"));
            ((ThreadStorage) client).setPhysicsThread(thread.get());
        });

        /* World Events */
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            MinecraftSpace space = MinecraftSpace.get(world);
            space.getEntityManager().tick();

            space.getRigidBodiesByClass(ElementRigidBody.class).forEach(rigidBody ->
                rigidBody.getFrame().from(rigidBody.getFrame(),
                        rigidBody.getPhysicsLocation(new Vector3f()),
                        rigidBody.getPhysicsRotation(new Quaternion()),
                        rigidBody.getCollisionShape().boundingBox(new Vector3f(), new Quaternion(), new BoundingBox())));

            if (!space.isEmpty() || space.isInPresim()) {
                thread.get().execute(space::step);
            }
        });

        BetterClientLifecycleEvents.LOAD_WORLD.register((client, world) -> {
            PhysicsSpaceEvents.PREINIT.invoker().onPreInit(thread.get(), world);
            ((SpaceStorage) world).putSpace(MinecraftSpace.MAIN, new MinecraftSpace(thread.get(), world));
            PhysicsSpaceEvents.INIT.invoker().onInit(thread.get(), MinecraftSpace.get(world));
        });
    }
}
