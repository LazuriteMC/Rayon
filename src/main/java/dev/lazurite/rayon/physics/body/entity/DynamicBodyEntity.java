package dev.lazurite.rayon.physics.body.entity;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.api.event.DynamicBodyCollisionEvent;
import dev.lazurite.rayon.api.shape.factory.EntityShapeFactory;
import dev.lazurite.rayon.physics.Rayon;
import dev.lazurite.rayon.physics.body.block.BlockRigidBody;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.List;

public class DynamicBodyEntity extends EntityRigidBody implements ComponentV3, CommonTickingComponent, AutoSyncedComponent {
    private final MinecraftDynamicsWorld dynamicsWorld;
    private float dragCoefficient;

    private final Quat4f targetOrientation;
    private final Vector3f targetPosition;
    private final Vector3f targetLinearVelocity;
    private final Vector3f targetAngularVelocity;

    private DynamicBodyEntity(Entity entity, RigidBodyConstructionInfo info, float dragCoefficient) {
        super(entity, info);
        this.dragCoefficient = dragCoefficient;

        this.targetOrientation = new Quat4f();
        this.targetPosition = new Vector3f();
        this.targetLinearVelocity = new Vector3f();
        this.targetAngularVelocity = new Vector3f();
        this.dynamicsWorld = MinecraftDynamicsWorld.get(entity.getEntityWorld());
    }

    public static DynamicBodyEntity create(Entity entity, EntityShapeFactory shapeFactory, float mass, float dragCoefficient) {
        /* Get the entity's shape */
        CollisionShape collisionShape = shapeFactory.create(entity);

        /* Calculate the inertia of the shape. */
        Vector3f inertia = new Vector3f();
        collisionShape.calculateLocalInertia(mass, inertia);

        /* Get the position of the entity. */
        Vector3f position = VectorHelper.vec3dToVector3f(entity.getPos());

        /* Calculate the new motion state. */
        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 1, 0, 0), position, 1.0f)));

        /* Create the Body based on the construction info. */
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(mass, motionState, collisionShape, inertia);
        DynamicBodyEntity physics = new DynamicBodyEntity(entity, constructionInfo, dragCoefficient);
        physics.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        return physics;
    }

    public static DynamicBodyEntity get(Entity entity) {
        try {
            return Rayon.DYNAMIC_BODY_ENTITY.get(entity);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean is(Entity entity) {
        return get(entity) != null;
    }

    @Override
    public void step(float delta) {
        super.step(delta);

        dynamicsWorld.getTouching(this).forEach(body -> {
            if (body instanceof BlockRigidBody) {
                DynamicBodyCollisionEvent.BLOCK_COLLISION.invoker().onBlockCollision((BlockRigidBody) body);
            } else if (body instanceof EntityRigidBody) {
                DynamicBodyCollisionEvent.ENTITY_COLLISION.invoker().onEntityCollision((EntityRigidBody) body);
            }
        });
    }

    @Override
    public void tick() {
        if (!isInWorld() && !entity.removed) {
            dynamicsWorld.addRigidBody(this);
        }

        if (dynamicsWorld.getWorld().isClient()) {
            setPosition(targetPosition);
            setLinearVelocity(targetLinearVelocity);
            setOrientation(targetOrientation);
            setAngularVelocity(targetAngularVelocity);
        } else {
            Rayon.DYNAMIC_BODY_ENTITY.sync(entity);
        }

        Vector3f position = getCenterOfMassPosition(new Vector3f());
        entity.updatePosition(position.x, position.y, position.z);
    }

    public void setDragCoefficient(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    public float getDragCoefficient() {
        return this.dragCoefficient;
    }

    public Quat4f getTargetOrientation(Quat4f out) {
        out.set(targetOrientation);
        return out;
    }

    public Vector3f getTargetPosition(Vector3f out) {
        out.set(targetPosition);
        return out;
    }

    public Vector3f getTargetLinearVelocity(Vector3f out) {
        out.set(targetLinearVelocity);
        return out;
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        targetOrientation.set(QuaternionHelper.fromBuffer(buf));
        targetPosition.set(VectorHelper.fromBuffer(buf));
        targetLinearVelocity.set(VectorHelper.fromBuffer(buf));
        targetAngularVelocity.set(VectorHelper.fromBuffer(buf));
        setDragCoefficient(buf.readFloat());
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        QuaternionHelper.toBuffer(buf, getOrientation(new Quat4f()));
        VectorHelper.toBuffer(buf, getCenterOfMassPosition(new Vector3f()));
        VectorHelper.toBuffer(buf, getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, getAngularVelocity(new Vector3f()));
        buf.writeFloat(getDragCoefficient());
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        setOrientation(QuaternionHelper.fromTag(tag.getCompound("orientation")));
        setPosition(VectorHelper.fromTag(tag.getCompound("position")));
        setLinearVelocity(VectorHelper.fromTag(tag.getCompound("linear_velocity")));
        setAngularVelocity(VectorHelper.fromTag(tag.getCompound("angular_velocity")));
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.put("orientation", QuaternionHelper.toTag(getOrientation(new Quat4f())));
        tag.put("position", VectorHelper.toTag(getCenterOfMassPosition(new Vector3f())));
        tag.put("linear_velocity", VectorHelper.toTag(getLinearVelocity(new Vector3f())));
        tag.put("angular_velocity", VectorHelper.toTag(getAngularVelocity(new Vector3f())));
    }
}
