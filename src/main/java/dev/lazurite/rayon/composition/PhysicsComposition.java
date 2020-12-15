package dev.lazurite.rayon.composition;

import dev.lazurite.rayon.side.server.ServerInitializer;
import dev.lazurite.rayon.type.PhysicsTypes;
import dev.lazurite.thimble.composition.Composition;
import dev.lazurite.thimble.synchronizer.Synchronizer;
import dev.lazurite.thimble.synchronizer.key.SynchronizedKey;
import dev.lazurite.thimble.synchronizer.type.SynchronizedTypeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class PhysicsComposition extends Composition {
    public static final Identifier IDENTIFIER = new Identifier(ServerInitializer.MODID, "physics");

    public static final SynchronizedKey<Integer> PLAYER_ID = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "player_id"), SynchronizedTypeRegistry.INTEGER, -1);
    public static final SynchronizedKey<Float> MASS = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "mass"), SynchronizedTypeRegistry.FLOAT, 0.0f);
    public static final SynchronizedKey<Float> DRAG_COEFFICIENT = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "drag_coefficient"), SynchronizedTypeRegistry.FLOAT, 0.0f);
    public static final SynchronizedKey<Boolean> NO_CLIP = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "no_clip"), SynchronizedTypeRegistry.BOOLEAN, false);

    public static final SynchronizedKey<Vector3f> POSITION = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "position"), PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Vector3f> LINEAR_VELOCITY = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "linear_velocity"), PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Vector3f> ANGULAR_VELOCITY = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "angular_velocity"), PhysicsTypes.VECTOR3F, new Vector3f());
    public static final SynchronizedKey<Quat4f> ORIENTATION = new SynchronizedKey<>(new Identifier(ServerInitializer.MODID, "orientation"), PhysicsTypes.QUAT4F, new Quat4f());

    public PhysicsComposition(Synchronizer synchronizer) {
        super(synchronizer);
    }

    @Override
    public void onTick(Entity entity) {
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
