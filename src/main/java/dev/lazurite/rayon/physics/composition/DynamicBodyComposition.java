package dev.lazurite.rayon.physics.composition;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.physics.PhysicsWorld;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.init.ServerInitializer;
import dev.lazurite.rayon.util.PhysicsTypes;
import dev.lazurite.thimble.composition.Composition;
import dev.lazurite.thimble.synchronizer.Synchronizer;
import dev.lazurite.thimble.synchronizer.key.SynchronizedKey;
import dev.lazurite.thimble.synchronizer.type.SynchronizedTypeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class DynamicBodyComposition extends Composition {
    public static final Identifier IDENTIFIER = new Identifier(ServerInitializer.MODID, "dynamic_body");

    public static final SynchronizedKey<Integer> PLAYER_ID = Synchronizer.register(new Identifier(ServerInitializer.MODID, "player_id"), SynchronizedTypeRegistry.INTEGER, -1);
    public static final SynchronizedKey<Float> MASS = Synchronizer.register(new Identifier(ServerInitializer.MODID, "mass"), SynchronizedTypeRegistry.FLOAT, 0.0f);
    public static final SynchronizedKey<Float> DRAG_COEFFICIENT = Synchronizer.register(new Identifier(ServerInitializer.MODID, "drag_coefficient"), SynchronizedTypeRegistry.FLOAT, 0.0f);
    public static final SynchronizedKey<Boolean> NO_CLIP = Synchronizer.register(new Identifier(ServerInitializer.MODID, "no_clip"), SynchronizedTypeRegistry.BOOLEAN, false);

    public static final SynchronizedKey<Vector3f> POSITION = Synchronizer.register(new Identifier(ServerInitializer.MODID, "position"), PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Vector3f> LINEAR_VELOCITY = Synchronizer.register(new Identifier(ServerInitializer.MODID, "linear_velocity"), PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Vector3f> ANGULAR_VELOCITY = Synchronizer.register(new Identifier(ServerInitializer.MODID, "angular_velocity"), PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Quat4f> ORIENTATION = Synchronizer.register(new Identifier(ServerInitializer.MODID, "orientation"), PhysicsTypes.QUAT4F, new Quat4f());

    private RigidBody rigidBody;

    public DynamicBodyComposition(Synchronizer synchronizer) {
        super(synchronizer);
    }

    @Override
    public void onTick(Entity entity) {
        World world = entity.getEntityWorld();
        entity.noClip = getSynchronizer().get(NO_CLIP);

        if (world.isClient()) {
            if (getRigidBody() == null) {
                createRigidBody(entity, null);
            }

            Quat4f orientation = getRigidBody().getOrientation(new Quat4f());
            QuaternionHelper.rotateY(orientation, 2);

            Transform trans = getRigidBody().getWorldTransform(new Transform());
            trans.setRotation(orientation);
            getRigidBody().setWorldTransform(trans);

            getSynchronizer().set(POSITION, getRigidBody().getCenterOfMassPosition(new Vector3f()));
            getSynchronizer().set(LINEAR_VELOCITY, getRigidBody().getLinearVelocity(new Vector3f()));
            getSynchronizer().set(ANGULAR_VELOCITY, getRigidBody().getAngularVelocity(new Vector3f()));
            getSynchronizer().set(ORIENTATION, getRigidBody().getOrientation(new Quat4f()));
        } else {
            /* Update the position of the entity based on the rigid body. */
            Vector3f position = new Vector3f(getSynchronizer().get(POSITION));
            entity.updatePosition(position.x, position.y, position.z);

            /* Update the orientation of the entity based on the rigid body. */
            Quat4f orientation = new Quat4f(getSynchronizer().get(ORIENTATION));

            entity.prevYaw = entity.yaw;
            entity.yaw = QuaternionHelper.getYaw(orientation);

            while (entity.yaw - entity.prevYaw < -180.0F) {
                entity.prevYaw -= 360.0F;
            }

            while (entity.yaw - entity.prevYaw >= 180.0F) {
                entity.prevYaw += 360.0F;
            }

            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).bodyYaw = entity.yaw;
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void step(Entity entity, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (this.belongsTo(client.player)) {

        }
    }

    /**
     * Creates a new {@link RigidBody} based off of the entity's attributes.
     * Creates a {@link BoxShape} if there is no {@link CollisionShape} specified.
     */
    @Environment(EnvType.CLIENT)
    public void createRigidBody(Entity entity, @Nullable CollisionShape shape) {
        /* Create a BoxShape if a shape isn't passed in. */
        if (shape == null) {
            Box bb = entity.getBoundingBox();
            shape = new BoxShape(new Vector3f(
                    (float) (bb.maxX - (bb.minX)),
                    (float) (bb.maxY - (bb.minY)),
                    (float) (bb.maxZ - (bb.minZ))
            ));
        }

        /* Calculate the inertia of the shape. */
        Vector3f inertia = new Vector3f(0.0F, 0.0F, 0.0F);
        shape.calculateLocalInertia(getSynchronizer().get(MASS), inertia);

        /* Get the position of the entity. */
        Vector3f position = VectorHelper.vec3dToVector3f(entity.getPos());

        /* Calculate the new/modified motion state. */
        DefaultMotionState motionState;
        if (getRigidBody() != null) {
            RigidBody old = getRigidBody();
            motionState = new DefaultMotionState(old.getWorldTransform(new Transform()));
            PhysicsWorld.getInstance().removeRigidBody(old);
        } else {
            motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 1, 0, 0), position, 1.0f)));
        }

        /* Create the RigidBody based on the construction info. */
        RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(getSynchronizer().get(MASS), motionState, shape, inertia);
        RigidBody rigidBody = new RigidBody(ci);

        /* Set the activation state so that deactivation is disabled. */
        rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        /* Set the class attribute. */
        this.rigidBody = rigidBody;

        getSynchronizer().set(POSITION, getRigidBody().getCenterOfMassPosition(new Vector3f()));
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
    public boolean onInteract(Entity entity, PlayerEntity player, Hand hand) {
        return false;
    }

    @Override
    public void onRemove(Entity entity) {
        System.out.println("BOOM. GONE");
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

    /**
     * Find whether or not this {@link DynamicBodyComposition}
     * belongs to given {@link PlayerEntity}
     * @param playerId the {@link PlayerEntity} Id
     * @return whether or not the {@link PlayerEntity} Id is equal to the synchronized Id
     */
    public boolean belongsTo(int playerId) {
        return getSynchronizer().get(PLAYER_ID).equals(playerId);
    }

    /**
     * Same as {@link DynamicBodyComposition#belongsTo(int)}, except
     * it passes the {@link PlayerEntity} Id number in.
     * @param player the {@link PlayerEntity}
     * @return whether or not the {@link PlayerEntity} is equal
     */
    public boolean belongsTo(PlayerEntity player) {
        return belongsTo(player.getEntityId());
    }

    /**
     * Start tracking all of the necessary
     * synchronized values.
     */
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

    /**
     * @return the {@link Identifier} used in packets and data tags
     */
    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }
}
