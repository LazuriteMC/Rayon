package dev.lazurite.rayon.entity.impl;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.thread.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.net.EntityElementMovementS2C;
import dev.lazurite.rayon.entity.impl.net.ElementPropertiesS2C;
import dev.lazurite.rayon.core.impl.thread.space.MinecraftSpace;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class RayonEntityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ElementPropertiesS2C.PACKET_ID, ElementPropertiesS2C::accept);
        ClientPlayNetworking.registerGlobalReceiver(EntityElementMovementS2C.PACKET_ID, EntityElementMovementS2C::accept);

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof EntityPhysicsElement) {
                MinecraftSpace space = MinecraftSpace.get(world);
                space.getThread().execute(() -> space.unload((EntityPhysicsElement) entity));
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(RayonEntityCommon.MODID, "element_spawn_s2c"), (client, handler, buf, sender) -> {
            int id = buf.readInt();
            UUID uuid = buf.readUuid();
            EntityType<?> type = Registry.ENTITY_TYPE.get(buf.readVarInt());
            Vector3f location = VectorHelper.fromBuffer(buf);
            Vector3f linearVelocity = VectorHelper.fromBuffer(buf);
            Vector3f angularVelocity = VectorHelper.fromBuffer(buf);
            Quaternion rotation = QuaternionHelper.fromBuffer(buf);

            client.execute(() -> {
                if (client.world != null) {
                    Entity entity = type.create(client.world);
                    ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();

                    entity.setEntityId(id);
                    entity.setUuid(uuid);
                    rigidBody.setPhysicsLocation(location);
                    rigidBody.setLinearVelocity(linearVelocity);
                    rigidBody.setAngularVelocity(angularVelocity);
                    rigidBody.setPhysicsRotation(rotation);

                    client.world.addEntity(id, entity);
                    PhysicsThread.get(client).execute(() -> MinecraftSpace.get(client.world).load((EntityPhysicsElement) entity));
                }
            });
        });
    }
}
