package dev.lazurite.api.physics.network.packet;

import dev.lazurite.api.physics.client.ClientInitializer;
import dev.lazurite.api.physics.server.ServerInitializer;
import dev.lazurite.api.physics.server.entity.PhysicsEntity;
import dev.lazurite.api.physics.client.handler.ClientPhysicsHandler;
import dev.lazurite.api.physics.client.handler.PhysicsHandler;
import dev.lazurite.api.physics.util.math.QuaternionHelper;
import dev.lazurite.api.physics.util.math.VectorHelper;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.stream.Stream;

/**
 * The packet responsible for sending entity physics information from the server to the client.
 * @author Ethan Johnson
 */
public class PhysicsHandlerS2C {
    public static final Identifier PACKET_ID = new Identifier(ServerInitializer.MODID, "entity_physics_s2c");

    /**
     * Accepts the packet. Server-side attributes are received from the server in this method including camera info,
     * physics info, and rate info.
     * @param context the packet context
     * @param buf the buffer containing the information
     */
    public static void accept(PacketContext context, PacketByteBuf buf) {
        PlayerEntity player = context.getPlayer();
        int entityID = buf.readInt();

        Vector3f position = VectorHelper.deserializeVector3f(buf);
        Vector3f linearVel = VectorHelper.deserializeVector3f(buf);
        Vector3f angularVel = VectorHelper.deserializeVector3f(buf);
        Quat4f orientation = QuaternionHelper.deserializeQuaternion(buf);

        context.getTaskQueue().execute(() -> {
            PhysicsEntity entity;
            ClientPhysicsHandler physics;

            if (player != null) {
                entity = (PhysicsEntity) player.world.getEntityById(entityID);

                if (entity != null && entity.getValue(PhysicsEntity.DIRTY)) {
                    entity.setValue(PhysicsEntity.DIRTY, false);
                    physics = (ClientPhysicsHandler) entity.getPhysics();

                    /* Physics Vectors (orientation, position, velocity, etc.) */
                    if (!physics.isActive() && entity.age > 10) {
                        physics.setPosition(position);
                        physics.getRigidBody().setLinearVelocity(linearVel);
                        physics.getRigidBody().setAngularVelocity(angularVel);
                        physics.setNetOrientation(orientation);
                    }
                }
            }
        });
    }

    /**
     * The method that send the drone information from the server to the client. Contains all
     * server-side values such as camera settings, physics settings, and rate settings.
     * @param physics the {@link PhysicsHandler} to send
     */
    public static void send(PhysicsHandler physics) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(physics.getEntity().getEntityId());

        /* Physics Vectors */
        VectorHelper.serializeVector3f(buf, physics.getPosition());
        VectorHelper.serializeVector3f(buf, physics.getLinearVelocity());
        VectorHelper.serializeVector3f(buf, physics.getAngularVelocity());
        QuaternionHelper.serializeQuaternion(buf, physics.getOrientation());

        Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(physics.getEntity().getEntityWorld(), new BlockPos(physics.getEntity().getPos()));
        watchingPlayers.forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PACKET_ID, buf));
    }

    /**
     * Registers the packet in {@link ClientInitializer}.
     */
    public static void register() {
        ClientSidePacketRegistry.INSTANCE.register(PACKET_ID, PhysicsHandlerS2C::accept);
    }
}
