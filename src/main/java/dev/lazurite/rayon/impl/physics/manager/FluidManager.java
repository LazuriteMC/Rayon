package dev.lazurite.rayon.impl.physics.manager;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.body.type.AirResistantBody;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import dev.lazurite.rayon.impl.util.config.Config;

import java.util.List;

public class FluidManager {
    private final MinecraftDynamicsWorld dynamicsWorld;

    public FluidManager(MinecraftDynamicsWorld dynamicsWorld) {
        this.dynamicsWorld = dynamicsWorld;
    }

    public void doAirResistance(List<AirResistantBody> rigidBodies) {
        if (Config.getInstance().getGlobal().isAirResistanceEnabled()) {
            rigidBodies.forEach(body -> doAirResistance((PhysicsRigidBody) body, body.getDragCoefficient()));
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
     * @param velocity the velocity of the {@link EntityRigidBody}
     * @param area the surface area of the {@link EntityRigidBody}
     * @param dragCoefficient the drag coefficient of the {@link EntityRigidBody}
     * @return the force vector of air resistance
     */
    public static Vector3f getForce(Vector3f velocity, float area, float dragCoefficient) {
        float k = (Config.getInstance().getGlobal().getAirDensity() * dragCoefficient * area) / 2.0f;
        Vector3f force = new Vector3f();
        force.set(velocity);
        force.multLocal(k);
        return force.multLocal(-velocity.lengthSquared());
    }
}
