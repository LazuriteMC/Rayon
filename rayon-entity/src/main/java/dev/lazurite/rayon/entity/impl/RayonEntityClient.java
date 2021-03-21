package dev.lazurite.rayon.entity.impl;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.thread.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.util.ElementPropertiesS2C;
import dev.lazurite.rayon.core.impl.thread.space.MinecraftSpace;
import dev.lazurite.rayon.entity.impl.util.ElementSpawnS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;

public class RayonEntityClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ElementPropertiesS2C.PACKET_ID, ElementPropertiesS2C::accept);
        ClientPlayNetworking.registerGlobalReceiver(ElementSpawnS2C.PACKET_ID, ElementSpawnS2C::accept);

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof EntityPhysicsElement) {
                MinecraftSpace space = MinecraftSpace.get(world);
                space.getThread().execute(() -> space.unload((EntityPhysicsElement) entity));
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(RayonEntityCommon.MOVEMENT_UPDATE, (client, handler, buf, sender) -> {
            if (client.world != null) {
                MinecraftSpace space = MinecraftSpace.get(client.world);

                int entityId = buf.readInt();
                Quaternion rotation = QuaternionHelper.fromBuffer(buf);
                Vector3f location = VectorHelper.fromBuffer(buf);
                Vector3f linearVelocity = VectorHelper.fromBuffer(buf);
                Vector3f angularVelocity = VectorHelper.fromBuffer(buf);

                if (space.getThread() != null) {
                    space.getThread().execute(() -> {
                        Entity entity = client.world.getEntityById(entityId);

                        if (entity instanceof EntityPhysicsElement) {
                            ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();

                            rigidBody.setPhysicsRotation(rotation);
                            rigidBody.setPhysicsLocation(location);
                            rigidBody.setLinearVelocity(linearVelocity);
                            rigidBody.setAngularVelocity(angularVelocity);
                            entity.updatePosition(location.x, location.y + rigidBody.boundingBox(new BoundingBox()).getYExtent(), location.z);
                        }
                    });
                }
            }
        });
    }
}
