package dev.lazurite.rayon.entity.impl;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * The client entry point for Rayon Entity. Handles the loading and unloading
 * of entities into the {@link MinecraftSpace} as well as {@link EntityPhysicsElement}
 * movement and property updates.
 * @see RayonEntityCommon
 */
public class RayonEntityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof EntityPhysicsElement) {
                PhysicsThread.get(world).execute(() -> MinecraftSpace.get(world).unload((EntityPhysicsElement) entity));
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(RayonEntityCommon.MOVEMENT_UPDATE, (client, handler, buf, sender) -> {
            int entityId = buf.readInt();
            RegistryKey<World> worldKey = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());

            Quaternion rotation = QuaternionHelper.fromBuffer(buf);
            Vector3f location = VectorHelper.fromBuffer(buf);
            Vector3f linearVelocity = VectorHelper.fromBuffer(buf);
            Vector3f angularVelocity = VectorHelper.fromBuffer(buf);

            PhysicsThread.get(client).execute(() -> {
                ClientWorld world = (ClientWorld) PhysicsThread.get(client).getWorldSupplier().getWorld(worldKey);

                if (world != null) {
                    Entity entity = world.getEntityById(entityId);

                    if (entity instanceof EntityPhysicsElement) {
                        ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();

                        rigidBody.setPhysicsRotation(rotation);
                        rigidBody.setPhysicsLocation(location);
                        rigidBody.setLinearVelocity(linearVelocity);
                        rigidBody.setAngularVelocity(angularVelocity);
                        rigidBody.activate();
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(RayonEntityCommon.PROPERTIES, (client, handler, buf, sender) -> {
            int entityId = buf.readInt();
            RegistryKey<World> worldKey = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());

            float mass = buf.readFloat();
            float dragCoefficient = buf.readFloat();
            float friction = buf.readFloat();
            float restitution = buf.readFloat();
            int blockDistance = buf.readInt();
            boolean doFluidResistance = buf.readBoolean();
            boolean doTerrainLoading = buf.readBoolean();
            boolean doEntityLoading = buf.readBoolean();
            UUID priorityPlayer = buf.readUuid();

            PhysicsThread.get(client).execute(() -> {
                ClientWorld world = (ClientWorld) PhysicsThread.get(client).getWorldSupplier().getWorld(worldKey);

                if (world != null) {
                    Entity entity = world.getEntityById(entityId);

                    if (entity instanceof EntityPhysicsElement) {
                        ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();
                        PlayerEntity player = world.getPlayerByUuid(priorityPlayer);

                        rigidBody.setMass(mass);
                        rigidBody.setDragCoefficient(dragCoefficient);
                        rigidBody.setFriction(friction);
                        rigidBody.setRestitution(restitution);
                        rigidBody.setEnvironmentLoadDistance(blockDistance);
                        rigidBody.setDoFluidResistance(doFluidResistance);
                        rigidBody.setDoTerrainLoading(doTerrainLoading);
                        rigidBody.setDoEntityLoading(doEntityLoading);
                        rigidBody.prioritize(player);
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(RayonEntityCommon.SPAWN, (client, handler, buf, sender) -> {
            int id = buf.readInt();
            UUID uuid = buf.readUuid();
            EntityType<?> type = Registry.ENTITY_TYPE.get(buf.readVarInt());
            RegistryKey<World> worldKey = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());

            Vector3f location = VectorHelper.fromBuffer(buf);
            Vector3f linearVelocity = VectorHelper.fromBuffer(buf);
            Vector3f angularVelocity = VectorHelper.fromBuffer(buf);
            Quaternion rotation = QuaternionHelper.fromBuffer(buf);

            client.execute(() -> {
                ClientWorld world = (ClientWorld) PhysicsThread.get(client).getWorldSupplier().getWorld(worldKey);

                if (world != null) {
                    Entity entity = type.create(world);

                    if (entity instanceof EntityPhysicsElement) {
                        ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();

                        entity.setEntityId(id);
                        entity.setUuid(uuid);

                        rigidBody.setPhysicsLocation(location);
                        rigidBody.setLinearVelocity(linearVelocity);
                        rigidBody.setAngularVelocity(angularVelocity);
                        rigidBody.setPhysicsRotation(rotation);
                        entity.updatePosition(location.x, location.y, location.z);

                        world.addEntity(id, entity);
                        PhysicsThread.get(client).execute(() -> MinecraftSpace.get(world).load((EntityPhysicsElement) entity));
                    }
                }
            });
        });
    }
}
