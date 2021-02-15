package dev.lazurite.rayon.impl.bullet.body.type;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.util.config.Config;

/**
 * Any {@link PhysicsRigidBody} with this interface assigned will be subject
 * to air resistance using it's drag coefficient value.
 */
public interface AirDragBody {
    float getDragCoefficient();

    default void applyAirDrag() {
        assert this instanceof PhysicsRigidBody : "Drag body must be rigid body.";

        PhysicsRigidBody rigidBody = (PhysicsRigidBody) this;
        float dragCoefficient = getDragCoefficient();
        float area = (float) Math.pow(rigidBody.boundingBox(new BoundingBox()).getExtent(new Vector3f()).lengthSquared(), 2);

        float k = (Config.getInstance().getAirDensity() * dragCoefficient * area) / 2.0f;
        Vector3f force = new Vector3f();
        force.set(rigidBody.getLinearVelocity(new Vector3f()));
        force.multLocal(k);
        rigidBody.applyCentralForce(force.multLocal(-rigidBody.getLinearVelocity(new Vector3f()).lengthSquared()));
    }
}
