package dev.lazurite.rayon.impl.bullet.body.packet;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncRigidBodyC2S {
    public static final Identifier PACKET_ID = new Identifier(Rayon.MODID, "sync_rigid_body_c2s");

    public static void accept(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        int entityId = buf.readInt();

        server.execute(() -> {
            Entity entity = player.getEntityWorld().getEntityById(entityId);

            if (entity instanceof PhysicsElement) {
                Rayon.THREAD.get(player.world).execute(space ->
                    ((PhysicsElement) entity).getRigidBody().deserialize(buf)
                );
            }
        });
    }

    public static void send(ElementRigidBody rigidBody) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(rigidBody.getElement().asEntity().getEntityId());
        rigidBody.serialize(buf);
        ClientPlayNetworking.send(PACKET_ID, buf);
    }
}
