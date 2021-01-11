package dev.lazurite.rayon.physics.body.entity;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import dev.lazurite.rayon.api.event.EntityBodyCollisionEvent;
import dev.lazurite.rayon.api.event.EntityBodyStepEvents;
import dev.lazurite.rayon.api.shape.factory.EntityShapeFactory;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.body.SteppableBody;
import dev.lazurite.rayon.physics.body.block.BlockRigidBody;
import dev.lazurite.rayon.physics.helper.AirHelper;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.function.BooleanSupplier;

/**
 * {@link EntityRigidBody} is the mainsail of Rayon. It's currently the only component
 * that you're able to register to an entity type using {@link DynamicEntityRegistry}. Not
 * only is it a CCA component, but it also represents a jBullet {@link RigidBody}. In this way,
 * it can be directly added toa jBullet {@link DiscreteDynamicsWorld}, or in this case, a
 * {@link MinecraftDynamicsWorld}.<br><br>
 *
 * Additionally, {@link EntityRigidBody} implements several interfaces which allow for more
 * functionality. {@link SteppableBody} allows the rigid body to be <i>stepped</i> during every
 * step of the {@link MinecraftDynamicsWorld}. {@link CommonTickingComponent} allows this class
 * to tick each time the provider ticks. {@link AutoSyncedComponent} is what is responsible for
 * sending packets containing position, velocity, orientation, etc. from the server to the client.<br><br>
 *
 * From an API user's standpoint, the only time you'll need to interact with this class is to
 * retrieve information from it (like position, velocity, orientation, etc). Otherwise, you can
 * modify it's behavior by registering an event in {@link EntityBodyCollisionEvent} or
 * {@link EntityBodyStepEvents}.<br><br>
 *
 * @see MinecraftDynamicsWorld
 * @see EntityBodyStepEvents
 * @see EntityBodyCollisionEvent
 */
public class EntityRigidBody extends RigidBody implements SteppableBody, ComponentV3, CommonTickingComponent, AutoSyncedComponent {
    private final MinecraftDynamicsWorld dynamicsWorld;
    private final Entity entity;
    private float dragCoefficient;
    private boolean noclip;

    private final Quat4f targetOrientation = new Quat4f();
    private final Quat4f previousOrientation = new Quat4f();
    private final Vector3f linearAcceleration = new Vector3f();
    private final Vector3f targetPosition = new Vector3f();
    private final Vector3f targetLinearVelocity = new Vector3f();
    private final Vector3f targetAngularVelocity = new Vector3f();

    private EntityRigidBody(Entity entity, RigidBodyConstructionInfo info, float dragCoefficient) {
        super(info);
        this.entity = entity;
        this.dragCoefficient = dragCoefficient;
        this.dynamicsWorld = MinecraftDynamicsWorld.get(entity.getEntityWorld());
        this.previousOrientation.set(getOrientation(new Quat4f()));
    }

    public static EntityRigidBody create(Entity entity, EntityShapeFactory shapeFactory, float mass, float dragCoefficient) {
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
        EntityRigidBody body = new EntityRigidBody(entity, constructionInfo, dragCoefficient);
        body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        return body;
    }

    public static EntityRigidBody get(Entity entity) {
        try {
            return Rayon.DYNAMIC_BODY_ENTITY.get(entity);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean is(Entity entity) {
        return get(entity) != null;
    }

    /**
     * Somewhat of a parallel to the traditional <i>tick</i>, this method is called every
     * time the physics simulation advances another step. The physics simulation can, but
     * doesn't always, run at Minecraft's traditional rate of 20 tps. The simulation can step
     * up to the same rate as the renderer. So don't rely on this method being called at a
     * constant rate, or any specific rate for that matter.<br><br>
     *
     * Also, it's important to note that mainly just physics related calls should be included here.
     * One example would be a call to {@link EntityRigidBody#applyCentralForce(Vector3f)} which
     * is best to do every step instead of every tick. The reason is that all forces to rigid bodies
     * are cleared after every step.<br><br>
     *
     * You can gain access to this method by registering an event handler in {@link EntityBodyStepEvents}.<br><br>
     * @param delta the amount of seconds since the last step
     * @see MinecraftDynamicsWorld#step(BooleanSupplier) 
     */
    @Override
    public void step(float delta) {
        /* Invoke all registered start step events */
        EntityBodyStepEvents.START_ENTITY_STEP.invoker().onStartStep(this);

        /* Invoke all collision events */
        dynamicsWorld.getTouching(this).forEach(body -> {
            if (body instanceof BlockRigidBody) {
                EntityBodyCollisionEvent.BLOCK_COLLISION.invoker().onBlockCollision(this, (BlockRigidBody) body);
            } else if (body instanceof EntityRigidBody) {
                EntityBodyCollisionEvent.ENTITY_COLLISION.invoker().onEntityCollision(this, (EntityRigidBody) body);
            }
        });

        /* Apply air resistance */
        applyCentralForce(AirHelper.getSimpleForce(this));

        /* Update linear acceleration */
        linearAcceleration.set(VectorHelper.mul(VectorHelper.sub(targetLinearVelocity, getLinearVelocity(new Vector3f())), delta));

        /* Invoke all registered end step events */
        EntityBodyStepEvents.END_ENTITY_STEP.invoker().onEndStep(this);
    }

    /**
     * A traditional tick method, this is simply called each time the entity
     * provider is ticked. It's responsible for doing a variety of things that
     * either don't require being called every physics step or <i>shouldn't</i> be
     * called every physics step.
     */
    @Override
    public void tick() {
        if (dynamicsWorld.getWorld().getChunkManager().shouldTickEntity(entity)) {
            if (!isInWorld()) {
                dynamicsWorld.addRigidBody(this);
            }
        } else if (isInWorld()) {
            dynamicsWorld.removeRigidBody(this);
        }

        previousOrientation.set(getOrientation(new Quat4f()));

        if (dynamicsWorld.getWorld().isClient()) {
            setPosition(targetPosition);
            setLinearVelocity(targetLinearVelocity);
            setOrientation(targetOrientation);
            setAngularVelocity(targetAngularVelocity);
        } else {
            Rayon.DYNAMIC_BODY_ENTITY.sync(entity);
        }

        Vector3f position = getCenterOfMassPosition(new Vector3f());
        entity.updatePosition(position.x, position.y - entity.getBoundingBox().getYLength() / 2.0f, position.z);

        entity.yaw = QuaternionHelper.getYaw(getOrientation(new Quat4f()));
        entity.pitch = QuaternionHelper.getPitch(getOrientation(new Quat4f()));
    }

    public void setDragCoefficient(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    public void setOrientation(Quat4f orientation) {
        worldTransform.setRotation(orientation);
    }

    public void setPosition(Vector3f position) {
        worldTransform.origin.set(position);
        entity.setPos(position.x, position.y, position.z);
    }

    public void setMass(float mass) {
        Vector3f inertia = new Vector3f();
        getCollisionShape().calculateLocalInertia(mass, inertia);
        this.setMassProps(mass, inertia);
    }

    public void setNoClip(boolean noclip) {
        this.noclip = noclip;
    }

    public Vector3f getLinearAcceleration(Vector3f out) {
        out.set(linearAcceleration);
        return out;
    }

    public float getDragCoefficient() {
        return this.dragCoefficient;
    }

    public Quat4f getTargetOrientation(Quat4f out) {
        out.set(targetOrientation);
        return out;
    }

    public Quat4f getPreviousOrientation(Quat4f out) {
        out.set(previousOrientation);
        return out;
    }

    public Box getBox() {
        Vector3f min = new Vector3f();
        Vector3f max = new Vector3f();
        Transform transform = new Transform();
        transform.setIdentity();
        getCollisionShape().getAabb(transform, min, max);
        return new Box(VectorHelper.vector3fToVec3d(min), VectorHelper.vector3fToVec3d(max));
    }

    public float getMass() {
        return 1.0f / this.getInvMass();
    }

    public boolean isNoClipEnabled() {
        return this.noclip;
    }

    public Entity getEntity() {
        return entity;
    }

    public MinecraftDynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
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
        setDragCoefficient(tag.getFloat("drag_coefficient"));
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.put("orientation", QuaternionHelper.toTag(getOrientation(new Quat4f())));
        tag.put("position", VectorHelper.toTag(getCenterOfMassPosition(new Vector3f())));
        tag.put("linear_velocity", VectorHelper.toTag(getLinearVelocity(new Vector3f())));
        tag.put("angular_velocity", VectorHelper.toTag(getAngularVelocity(new Vector3f())));
        tag.putFloat("drag_coefficient", getDragCoefficient());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityRigidBody) {
            return ((EntityRigidBody) obj).getEntity().equals(getEntity());
        }

        return false;
    }
}
