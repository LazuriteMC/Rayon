package dev.lazurite.rayon.entity.impl.util;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.RayonCoreCommon;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Handles the spawning of {@link EntityPhysicsElement}s. To use this with a
 * non-{@link LivingEntity}, call {@link ElementSpawnS2C#create(EntityPhysicsElement)}
 * within your {@link Entity#createSpawnPacket()} override.
 */
public class ElementSpawnS2C {
    public static final Identifier PACKET_ID = new Identifier(RayonCoreCommon.MODID, "element_spawn_s2c");

    public static void accept(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
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
        });
    }

    public static Packet<?> create(EntityPhysicsElement element) {
        ElementRigidBody rigidBody = element.getRigidBody();
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(element.asEntity().getEntityId());
        buf.writeUuid(element.asEntity().getUuid());
        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(element.asEntity().getType()));

        RegistryKey<World> worldKey = element.asEntity().getEntityWorld().getRegistryKey();
        buf.writeIdentifier(worldKey.getValue());

        VectorHelper.toBuffer(buf, VectorHelper.vec3dToVector3f(element.asEntity().getPos()));
        VectorHelper.toBuffer(buf, rigidBody.getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, rigidBody.getAngularVelocity(new Vector3f()));
        QuaternionHelper.toBuffer(buf, rigidBody.getPhysicsRotation(new Quaternion()));

        return ServerPlayNetworking.createS2CPacket(PACKET_ID, buf);
    }
}
