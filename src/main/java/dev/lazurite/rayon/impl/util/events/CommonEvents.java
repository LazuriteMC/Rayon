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
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

public class CommonEvents {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> Rayon.SERVER_THREAD = new PhysicsThread(server, "Server Physics Thread"));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> Rayon.SERVER_THREAD.destroy());
        ServerTickEvents.END_SERVER_TICK.register(server -> Rayon.SERVER_THREAD.tick());

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof PhysicsElement) {
                MinecraftSpace space = Rayon.SPACE.get(entity.getEntityWorld());

                /* Set the position of the rigid body */
                ElementRigidBody rigidBody = ((PhysicsElement) entity).getRigidBody();
                rigidBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos().add(0, rigidBody.boundingBox(new BoundingBox()).getYExtent(), 0)));
                rigidBody.setPhysicsRotation(QuaternionHelper.rotateY(new Quaternion(), -entity.yaw));

                Rayon.SERVER_THREAD.execute(() -> {
                    if (!space.getRigidBodyList().contains(((PhysicsElement) entity).getRigidBody())) {
                        space.addCollisionObject(((PhysicsElement) entity).getRigidBody());
                    }

                    ((PhysicsElement) entity).getRigidBody().activate();
                });
            }
        });

        EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
            if (entity instanceof PhysicsElement) {
                MinecraftSpace space = Rayon.SPACE.get(entity.getEntityWorld());

                Rayon.SERVER_THREAD.execute(() -> {
                    if (!space.getRigidBodyList().contains(((PhysicsElement) entity).getRigidBody())) {
                        space.addCollisionObject(((PhysicsElement) entity).getRigidBody());
                    }

                    ((PhysicsElement) entity).getRigidBody().activate();
                });
            }
        });

        EntityTrackingEvents.STOP_TRACKING.register((entity, player) -> {
            if (entity instanceof PhysicsElement && PlayerLookup.tracking(entity).isEmpty()) {
                MinecraftSpace space = Rayon.SPACE.get(entity.getEntityWorld());

                if (space.getRigidBodyList().contains(((PhysicsElement) entity).getRigidBody())) {
                    Rayon.SERVER_THREAD.execute(() -> space.removeCollisionObject(((PhysicsElement) entity).getRigidBody()));
                }
            }
        });
    }
}
