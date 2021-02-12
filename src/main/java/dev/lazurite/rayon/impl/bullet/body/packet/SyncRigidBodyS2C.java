package dev.lazurite.rayon.impl.bullet.body.packet;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncRigidBodyS2C {
    public static final Identifier PACKET_ID = new Identifier(Rayon.MODID, "sync_rigid_body_s2c");

    public static void accept(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        int entityId = buf.readInt();

        client.execute(() -> {
            if (client.world != null) {
                Entity entity = client.world.getEntityById(entityId);

                if (entity instanceof PhysicsElement) {
                    Rayon.THREAD.get(client.world).execute(space ->
                        ((PhysicsElement) entity).getRigidBody().deserialize(buf)
                    );
                }
            }
        });
    }

    public static void send(ServerPlayerEntity player, ElementRigidBody rigidBody) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(rigidBody.getElement().asEntity().getEntityId());
        rigidBody.serialize(buf);
        ServerPlayNetworking.send(player, PACKET_ID, buf);
    }
}
