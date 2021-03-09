package dev.lazurite.rayon.impl.util.events;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ClientEvents {
    public static void register() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> Rayon.CLIENT_THREAD = new PhysicsThread(client, "Client Physics Thread"));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Rayon.CLIENT_THREAD.destroy());
        ClientTickEvents.END_WORLD_TICK.register(client -> Rayon.CLIENT_THREAD.tick());

        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof PhysicsElement) {
                MinecraftSpace space = Rayon.SPACE.get(entity.getEntityWorld());
                ElementRigidBody rigidBody = ((PhysicsElement) entity).getRigidBody();
                rigidBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos().add(0, rigidBody.boundingBox(new BoundingBox()).getYExtent(), 0)));
                rigidBody.setPhysicsRotation(QuaternionHelper.rotateY(new Quaternion(), -entity.yaw));

                Rayon.CLIENT_THREAD.execute(() -> {
                    if (!space.getRigidBodyList().contains(((PhysicsElement) entity).getRigidBody())) {
                        space.addCollisionObject(((PhysicsElement) entity).getRigidBody());
                    } else {
                        ((PhysicsElement) entity).getRigidBody().activate();
                    }
                });
            }
        });

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof PhysicsElement) {
                MinecraftSpace space = Rayon.SPACE.get(entity.getEntityWorld());

                if (space.getRigidBodyList().contains(((PhysicsElement) entity).getRigidBody())) {
                    Rayon.CLIENT_THREAD.execute(() -> space.removeCollisionObject(((PhysicsElement) entity).getRigidBody()));
                }
            }
        });
    }
}
