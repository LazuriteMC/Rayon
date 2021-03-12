package dev.lazurite.rayon.impl.bullet.body.net;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.impl.mixin.common.EntityMixin;
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
import net.minecraft.world.World;

/**
 * This packet is very similar to {@link ElementMovementS2C} with the main difference
 * of this packet sends information from the <b>client</b> to the <b>server</b>. It's a reverse
 * packet that's only used when an element has a <i>priority player</i> stored. The stored
 * priority player is responsible for sending movement updates to the server using this packet.
 * @see ElementMovementS2C
 * @see EntityMixin
 */
public class ElementMovementC2S {
    public static final Identifier PACKET_ID = new Identifier(Rayon.MODID, "entity_element_movement_c2s");

    public static void accept(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        World world = player.getEntityWorld();
        int entityId = buf.readInt();
        Quaternion rotation = QuaternionHelper.fromBuffer(buf);
        Vector3f location = VectorHelper.fromBuffer(buf);
        Vector3f linearVelocity = VectorHelper.fromBuffer(buf);
        Vector3f angularVelocity = VectorHelper.fromBuffer(buf);

        MinecraftSpace.get(world).getThread().execute(() -> {
            Entity entity = world.getEntityById(entityId);

            if (entity instanceof PhysicsElement) {
                ElementRigidBody rigidBody = ((PhysicsElement) entity).getRigidBody();

                if (player.equals(rigidBody.getPriorityPlayer())) {
                    rigidBody.setPhysicsRotation(rotation);
                    rigidBody.setPhysicsLocation(location);
                    rigidBody.setLinearVelocity(linearVelocity);
                    rigidBody.setAngularVelocity(angularVelocity);
                }
            }
        });
    }

    public static void send(PhysicsElement element) {
        assert element instanceof Entity : "Element must be an entity.";

        ElementRigidBody rigidBody = element.getRigidBody();
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(element.asEntity().getEntityId());
        QuaternionHelper.toBuffer(buf, rigidBody.getPhysicsRotation(new Quaternion()));
        VectorHelper.toBuffer(buf, rigidBody.getPhysicsLocation(new Vector3f()));
        VectorHelper.toBuffer(buf, rigidBody.getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, rigidBody.getAngularVelocity(new Vector3f()));

        ClientPlayNetworking.send(PACKET_ID, buf);
    }
}
