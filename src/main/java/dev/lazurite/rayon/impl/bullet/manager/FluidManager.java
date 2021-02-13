package dev.lazurite.rayon.impl.bullet.manager;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.body.type.CustomDragBody;
import dev.lazurite.rayon.impl.util.config.Config;

import java.util.Collection;

public class FluidManager {
    public void doAirResistance(Collection<PhysicsRigidBody> rigidBodies) {
        if (Config.getInstance().isAirResistanceEnabled()) {
            rigidBodies.forEach(body -> {
                if (body instanceof CustomDragBody) {
                    doAirResistance(body, ((CustomDragBody) body).getDragCoefficient());
                } else {
                    doAirResistance(body, 0.05f);
                }
            });
        }
    }

    public void doAirResistance(PhysicsRigidBody rigidBody, float dragCoefficient) {
        rigidBody.applyCentralForce(getForce(
                rigidBody.getLinearVelocity(new Vector3f()),
                (float) Math.pow(rigidBody.boundingBox(new BoundingBox()).getExtent(new Vector3f()).lengthSquared(), 2),
                dragCoefficient
        ));
    }

    /**
     * Performs the basic calculations that are common to all air
     * resistance calculation types. It uses a simple drag formula.
     * @param velocity the velocity of the {@link PhysicsRigidBody}
     * @param area the surface area of the {@link PhysicsRigidBody}
     * @param dragCoefficient the drag coefficient of the {@link PhysicsRigidBody}
     * @return the force vector of air resistance
     */
    public static Vector3f getForce(Vector3f velocity, float area, float dragCoefficient) {
        float k = (Config.getInstance().getAirDensity() * dragCoefficient * area) / 2.0f;
        Vector3f force = new Vector3f();
        force.set(velocity);
        force.multLocal(k);
        return force.multLocal(-velocity.lengthSquared());
    }
}
