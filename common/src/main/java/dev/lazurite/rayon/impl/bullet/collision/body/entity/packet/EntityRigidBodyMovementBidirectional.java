package dev.lazurite.rayon.impl.bullet.collision.body.entity.packet;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public class EntityRigidBodyMovementBidirectional {
    private final int entityId;
    private final Quaternion rotation;
    private final Vector3f position;
    private final Vector3f linearVelocity;
    private final Vector3f angularVelocity;

    public EntityRigidBodyMovementBidirectional(EntityRigidBody rigidBody) {
        this(rigidBody.getElement().cast().getId(), rigidBody.getPhysicsRotation(new Quaternion()), rigidBody.getPhysicsLocation(new Vector3f()), rigidBody.getLinearVelocity(new Vector3f()), rigidBody.getAngularVelocity(new Vector3f()));
    }

    public EntityRigidBodyMovementBidirectional(int entityId, Quaternion rotation, Vector3f position, Vector3f linearVelocity, Vector3f angularVelocity) {
        this.entityId = entityId;
        this.rotation = rotation;
        this.position = position;
        this.linearVelocity = linearVelocity;
        this.angularVelocity = angularVelocity;
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        QuaternionHelper.toBuffer(buf, Convert.toMinecraft(rotation));
        VectorHelper.toBuffer(buf, Convert.toMinecraft(position));
        VectorHelper.toBuffer(buf, Convert.toMinecraft(linearVelocity));
        VectorHelper.toBuffer(buf, Convert.toMinecraft(angularVelocity));
        return buf;
    }

    public static EntityRigidBodyMovementBidirectional decode(FriendlyByteBuf buf) {
        return new EntityRigidBodyMovementBidirectional(
                buf.readInt(),
                Convert.toBullet(QuaternionHelper.fromBuffer(buf)),
                Convert.toBullet(VectorHelper.fromBuffer(buf)),
                Convert.toBullet(VectorHelper.fromBuffer(buf)),
                Convert.toBullet(VectorHelper.fromBuffer(buf))
        );
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getLinearVelocity() {
        return linearVelocity;
    }

    public Vector3f getAngularVelocity() {
        return angularVelocity;
    }

    public static void accept(EntityRigidBodyMovementBidirectional packet, Level level){
        final var entity = level.getEntity(packet.getEntityId());

        if (entity instanceof EntityPhysicsElement element) {
            final var rigidBody = element.getRigidBody();

            rigidBody.setPhysicsRotation(packet.getRotation());
            rigidBody.setPhysicsLocation(packet.getPosition());
            rigidBody.setLinearVelocity(packet.getLinearVelocity());
            rigidBody.setAngularVelocity(packet.getAngularVelocity());
            rigidBody.activate();
        }
    }
}