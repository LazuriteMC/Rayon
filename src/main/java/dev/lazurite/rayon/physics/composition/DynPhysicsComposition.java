package dev.lazurite.rayon.physics.composition;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.physics.PhysicsWorld;
import dev.lazurite.rayon.physics.helper.QuaternionHelper;
import dev.lazurite.rayon.server.ServerInitializer;
import dev.lazurite.rayon.util.PhysicsTypes;
import dev.lazurite.thimble.composition.Composition;
import dev.lazurite.thimble.synchronizer.Synchronizer;
import dev.lazurite.thimble.synchronizer.key.SynchronizedKey;
import dev.lazurite.thimble.synchronizer.type.SynchronizedTypeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class DynPhysicsComposition extends Composition {
    public static final Identifier IDENTIFIER = new Identifier(ServerInitializer.MODID, "dynamic_physics");

    public static final SynchronizedKey<Integer> PLAYER_ID = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "player_id"), SynchronizedTypeRegistry.INTEGER, -1);
    public static final SynchronizedKey<Float> MASS = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "mass"), SynchronizedTypeRegistry.FLOAT, 0.0f);
    public static final SynchronizedKey<Float> DRAG_COEFFICIENT = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "drag_coefficient"), SynchronizedTypeRegistry.FLOAT, 0.0f);
    public static final SynchronizedKey<Boolean> NO_CLIP = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "no_clip"), SynchronizedTypeRegistry.BOOLEAN, false);

    public static final SynchronizedKey<Vector3f> POSITION = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "position"), PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Vector3f> LINEAR_VELOCITY = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "linear_velocity"), PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Vector3f> ANGULAR_VELOCITY = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "angular_velocity"), PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Quat4f> ORIENTATION = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "orientation"), PhysicsTypes.QUAT4F, new Quat4f());

    private RigidBody rigidBody;

    public DynPhysicsComposition(Synchronizer synchronizer) {
        super(synchronizer);
    }

    @Override
    public void onTick(Entity entity) {
        World world = entity.getEntityWorld();
        Quat4f orientation = new Quat4f(getSynchronizer().get(ORIENTATION));
        Vector3f position = new Vector3f(getSynchronizer().get(POSITION));

        entity.updatePosition(position.x, position.y, position.z);

        entity.prevYaw = entity.yaw;
        entity.yaw = QuaternionHelper.getYaw(orientation);

        while(entity.yaw - entity.prevYaw < -180.0F) {
            entity.prevYaw -= 360.0F;
        }

        while(entity.yaw - entity.prevYaw >= 180.0F) {
            entity.prevYaw += 360.0F;
        }

        if (world.isClient()) {
//            ClientPhysicsHandler physics = (ClientPhysicsHandler) this.physics;
//            physics.updateNetOrientation();
        } else {
//            if (getValue(PLAYER_ID) != -1 && getEntityWorld().getEntityById(getValue(PLAYER_ID)) == null) {
//                kill();
//            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void step(Entity entity, float delta) {

    }

    /**
     * Creates a new {@link RigidBody} based off of the entity's attributes.
     */
    @Environment(EnvType.CLIENT)
    public void createRigidBody(Entity entity) {
        Vector3f inertia = new Vector3f(0.0F, 0.0F, 0.0F);
        shape.calculateLocalInertia(getSynchronizer().get(MASS), inertia);

        Vec3d pos = entity.getPos();
        Vector3f position = new Vector3f((float) pos.x, (float) pos.y + 0.125f, (float) pos.z);

        DefaultMotionState motionState;
        if (getRigidBody() != null) {
            RigidBody old = getRigidBody();
            motionState = new DefaultMotionState(old.getWorldTransform(new Transform()));
            PhysicsWorld.getInstance().removeRigidBody(old);
        } else {
            motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 1, 0, 0), position, 1.0f)));
        }

        RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(getSynchronizer().get(MASS), motionState, shape, inertia);
        RigidBody body = new RigidBody(ci);
        body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        this.body = body;
    }

    @Environment(EnvType.CLIENT)
    public RigidBody getRigidBody() {
        return this.rigidBody;
    }

    @Environment(EnvType.CLIENT)
    public boolean isHost(Entity entity) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null) {
            return entity.age > 5 && getSynchronizer().get(PLAYER_ID) == player.getEntityId();
        }

        return false;
    }

    @Environment(EnvType.CLIENT)
    public void applyForce(Vector3f... forces) {
        for (Vector3f force : forces) {
            getRigidBody().applyCentralForce(force);
        }
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand) {
        return false;
    }

    @Override
    public void onRemove() {

    }

    /**
     * Rotate the component's {@link Quat4f} by the given degrees on the X axis.
     * @param deg degrees to rotate by
     */
    public void rotateX(float deg) {
        Quat4f quat = getSynchronizer().get(ORIENTATION);
        QuaternionHelper.rotateX(quat, deg);
        getSynchronizer().set(ORIENTATION, quat);
    }

    /**
     * Rotate the component's {@link Quat4f} by the given degrees on the Y axis.
     * @param deg degrees to rotate by
     */
    public void rotateY(float deg) {
        Quat4f quat = getSynchronizer().get(ORIENTATION);
        QuaternionHelper.rotateY(quat, deg);
        getSynchronizer().set(ORIENTATION, quat);
    }

    /**
     * Rotate the component's {@link Quat4f} by the given degrees on the Z axis.
     * @param deg degrees to rotate by
     */
    public void rotateZ(float deg) {
        Quat4f quat = getSynchronizer().get(ORIENTATION);
        QuaternionHelper.rotateZ(quat, deg);
        getSynchronizer().set(ORIENTATION, quat);
    }

    @Override
    public void initSynchronizer() {
        getSynchronizer().track(PLAYER_ID);
        getSynchronizer().track(MASS);
        getSynchronizer().track(DRAG_COEFFICIENT);
        getSynchronizer().track(NO_CLIP);

        getSynchronizer().track(POSITION);
        getSynchronizer().track(LINEAR_VELOCITY);
        getSynchronizer().track(ANGULAR_VELOCITY);
        getSynchronizer().track(ORIENTATION);
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }
}
