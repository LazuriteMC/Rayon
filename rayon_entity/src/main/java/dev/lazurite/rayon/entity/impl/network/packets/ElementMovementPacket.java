package dev.lazurite.rayon.entity.impl.network.packets;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import dev.lazurite.toolbox.api.QuaternionHelper;
import dev.lazurite.toolbox.api.VectorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class ElementMovementPacket {
    private final int entityId;
    private final Quaternion rotation;
    private final Vector3f location;
    private final Vector3f linearVelocity;
    private final Vector3f angularVelocity;

    public ElementMovementPacket(int entityId, Quaternion rotation, Vector3f location, Vector3f linearVelocity, Vector3f angularVelocity) {
        this.entityId = entityId;
        this.rotation = rotation;
        this.location = location;
        this.linearVelocity = linearVelocity;
        this.angularVelocity = angularVelocity;
    }

    public ElementMovementPacket(EntityRigidBody body) {
        this.entityId = body.getElement().asEntity().getId();
        this.rotation = body.getPhysicsRotation(new Quaternion());
        this.location = body.getPhysicsLocation(new Vector3f());
        this.linearVelocity = body.getLinearVelocity(new Vector3f());
        this.angularVelocity = body.getAngularVelocity(new Vector3f());
    }

    public static ElementMovementPacket decode(FriendlyByteBuf buf) {
        final var entityId = buf.readInt();
        final var rotation = Convert.toBullet(QuaternionHelper.fromBuffer(buf));
        final var location = Convert.toBullet(VectorHelper.fromBuffer(buf));
        final var linearVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        final var angularVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        return new ElementMovementPacket(entityId, rotation, location, linearVelocity, angularVelocity);
    }

    public static void handle(ElementMovementPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayer serverPlayer = context.getSender();
            PhysicsThread.getOptional(serverPlayer.getServer()).ifPresent(thread -> thread.execute(() -> {
                final var level = serverPlayer.getLevel();
                final var entity = level.getEntity(packet.entityId);

                if (entity instanceof EntityPhysicsElement element) {
                    final var rigidBody = element.getRigidBody();

                    if (serverPlayer.equals(rigidBody.getPriorityPlayer())) {
                        rigidBody.setPhysicsRotation(packet.rotation);
                        rigidBody.setPhysicsLocation(packet.location);
                        rigidBody.setLinearVelocity(packet.linearVelocity);
                        rigidBody.setAngularVelocity(packet.angularVelocity);
                        rigidBody.activate();
                        rigidBody.sendMovementPacket();
                    }
                }
            }));
        } else if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            Minecraft minecraft = Minecraft.getInstance();
            PhysicsThread.getOptional(minecraft).ifPresent(thread -> thread.execute(() -> {
                if (minecraft.player != null) {
                    final var level = minecraft.player.clientLevel;
                    final var entity = level.getEntity(packet.entityId);

                    if (entity instanceof EntityPhysicsElement element) {
                        final var rigidBody = element.getRigidBody();
                        rigidBody.setPhysicsRotation(packet.rotation);
                        rigidBody.setPhysicsLocation(packet.location);
                        rigidBody.setLinearVelocity(packet.linearVelocity);
                        rigidBody.setAngularVelocity(packet.angularVelocity);
                        rigidBody.activate();
                    }
                }
            }));
        }
        context.setPacketHandled(true);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        QuaternionHelper.toBuffer(buf, Convert.toMinecraft(rotation));
        VectorHelper.toBuffer(buf, Convert.toMinecraft(location));
        VectorHelper.toBuffer(buf, Convert.toMinecraft(linearVelocity));
        VectorHelper.toBuffer(buf, Convert.toMinecraft(angularVelocity));
    }

}
