package dev.lazurite.rayon.impl.util.net;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class RigidBodyC2S {
    public static final Identifier PACKET_ID = new Identifier(Rayon.MODID, "rigid_body_c2s");

    public static void accept(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        int entityId = buf.readInt();
        Quaternion rotation = QuaternionHelper.fromBuffer(buf);
        Vector3f location = VectorHelper.fromBuffer(buf);
        Vector3f linearVelocity = VectorHelper.fromBuffer(buf);
        Vector3f angularVelocity = VectorHelper.fromBuffer(buf);

        server.execute(() -> {
            Entity entity = player.getEntityWorld().getEntityById(entityId);

            if (Rayon.ENTITY.maybeGet(entity).isPresent()) {
                EntityRigidBody rigidBody = Rayon.ENTITY.get(entity);

                rigidBody.setPhysicsRotation(rotation);
                rigidBody.setPhysicsLocation(location);
                rigidBody.setLinearVelocity(linearVelocity);
                rigidBody.getAngularVelocity(angularVelocity);
            }
        });
    }

    public static void send(EntityRigidBody rigidBody) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(rigidBody.getEntity().getEntityId());
        QuaternionHelper.toBuffer(buf, rigidBody.getPhysicsRotation(new Quaternion()));
        VectorHelper.toBuffer(buf, rigidBody.getPhysicsLocation(new Vector3f()));
        VectorHelper.toBuffer(buf, rigidBody.getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, rigidBody.getAngularVelocity(new Vector3f()));
        ClientPlayNetworking.send(PACKET_ID, buf);
    }
}
