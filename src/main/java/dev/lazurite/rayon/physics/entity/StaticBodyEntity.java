package dev.lazurite.rayon.physics.entity;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.api.shape.EntityBoxShape;
import dev.lazurite.rayon.physics.Rayon;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class StaticBodyEntity extends EntityRigidBody implements ComponentV3, CommonTickingComponent, AutoSyncedComponent {
    private final MinecraftDynamicsWorld dynamicsWorld;

    private StaticBodyEntity(Entity entity, RigidBodyConstructionInfo info) {
        super(entity, info);
        this.dynamicsWorld = MinecraftDynamicsWorld.get(entity.getEntityWorld());
    }

    public static StaticBodyEntity create(Entity entity) {
        /* Create the entity's shape */
        CollisionShape collisionShape = new EntityBoxShape(entity);

        /* Get the position of the entity. */
        Vector3f position = VectorHelper.vec3dToVector3f(entity.getPos());

        /* Calculate the new motion state. */
        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 1, 0, 0), position, 1.0f)));

        /* Create the Body based on the construction info. */
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(0, motionState, collisionShape, new Vector3f());
        StaticBodyEntity physics = new StaticBodyEntity(entity, constructionInfo);
        physics.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        return physics;
    }

    public static StaticBodyEntity get(Entity entity) {
        try {
            return Rayon.STATIC_BODY_ENTITY.get(entity);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void step(float delta) {
        if (!isInWorld()) {
            dynamicsWorld.addRigidBody(this);
        }
    }

    @Override
    public void tick() {
        setPosition(VectorHelper.vec3dToVector3f(entity.getPos().add(new Vec3d(0, entity.getBoundingBox().getYLength() / 2.0, 0))));
    }

    @Override
    public void readFromNbt(CompoundTag tag) {

    }

    @Override
    public void writeToNbt(CompoundTag tag) {

    }
}
