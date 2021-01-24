package dev.lazurite.rayon.api.packet;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.util.exception.RayonSpawnException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * This custom spawn packet can only be used with physics entities. It isn't required in order to
 * spawn your custom entity but it is highly recommended since it handles the transfer of data such
 * as position, orientation, velocity, etc.<br><br>
 *
 * To use this, just call {@link RayonSpawnS2CPacket#get} within your {@link Entity#createSpawnPacket()} method.
 * @since 1.0.0
 */
public class RayonSpawnS2CPacket {
    public static final Identifier PACKET_ID = new Identifier(Rayon.MODID, "rayon_spawn_s2c_packet");

    public static Packet<?> get (Entity entity){
        if (!EntityRigidBody.is(entity)) {
            throw new RayonSpawnException("The given entity is not registered.");
        }

        PacketByteBuf buf = PacketByteBufs.create();
        EntityRigidBody body = EntityRigidBody.get(entity);

        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
        buf.writeInt(entity.getEntityId());
        buf.writeUuid(entity.getUuid());

        /* If the user didn't set the position of the entity using EntityRigidBody#setPosition, then set it to the entity's position instead */
        if (body.getCenterOfMassPosition(new Vector3f()).equals(new Vector3f(0, 0, 0))) {
            body.setPosition(VectorHelper.vec3dToVector3f(entity.getPos().add(0, 1, 0)));
        }

        QuaternionHelper.toBuffer(buf, body.getOrientation(new Quat4f()));
        VectorHelper.toBuffer(buf, body.getCenterOfMassPosition(new Vector3f()));
        VectorHelper.toBuffer(buf, body.getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, body.getAngularVelocity(new Vector3f()));

        return ServerPlayNetworking.createS2CPacket(PACKET_ID, buf);
    }
}
