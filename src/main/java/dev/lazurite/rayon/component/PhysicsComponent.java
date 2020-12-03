package dev.lazurite.rayon.component;

import dev.lazurite.thimble.component.Component;
import dev.lazurite.thimble.synchronizer.SynchronizedKey;
import dev.lazurite.thimble.synchronizer.type.SynchronizedTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public abstract class PhysicsComponent extends Component<Entity> {
    public static final SynchronizedKey<Integer> PLAYER_ID = new SynchronizedKey<>(SynchronizedTypes.INTEGER, -1, (component, value) -> {
        Entity entity = (Entity) component.getOwner();
        PlayerEntity player = entity.getEntityWorld().getEntityById(playerID);
        if (entity instanceof PlayerEntity) {
            setCustomName(new LiteralText(((PlayerEntity) entity).getGameProfile().getName()));
            setCustomNameVisible(true);
        }
    });
    public static final SynchronizedKey<Float> MASS = new SynchronizedKey<>(SynchronizedTypes.FLOAT, 0.0f);
    public static final SynchronizedKey<Float> DRAG_COEFFICIENT = new SynchronizedKey<>(SynchronizedTypes.FLOAT, 0.0f);
    public static final SynchronizedKey<Boolean> NO_CLIP = new SynchronizedKey<>(SynchronizedTypes.BOOLEAN, false);

    public static final SynchronizedKey<Vector3f> POSITION = new SynchronizedKey<>(PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Vector3f> LINEAR_VELOCITY = new SynchronizedKey<>(PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Vector3f> ANGULAR_VELOCITY = new SynchronizedKey<>(PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Quat4f> ORIENTATION = new SynchronizedKey<>(PhysicsTypes.QUAT4F, new Quat4f());

    public PhysicsComponent(Entity owner) {
        super(owner);
    }

    @Override
    public void tick() {
        Entity entity = getOwner();
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
            ClientPhysicsHandler physics = (ClientPhysicsHandler) this.physics;
            physics.updateNetOrientation();
        } else {
            if (getValue(PLAYER_ID) != -1 && getEntityWorld().getEntityById(getValue(PLAYER_ID)) == null) {
//                kill();
            }
        }
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
}
