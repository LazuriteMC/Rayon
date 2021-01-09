package dev.lazurite.rayon.physics.helper;

import dev.lazurite.rayon.physics.body.entity.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.util.config.Config;
import dev.lazurite.rayon.util.config.ConfigScreen;

import javax.vecmath.Vector3f;
import java.util.function.Function;

public class AirHelper {
    /**
     * The type of air resistance calculations to perform is
     * controlled using this enumerator. The name {@link String} is used
     * when presented on the {@link ConfigScreen}. The {@link Function}
     * contains a reference to which method should be called during
     * {@link EntityRigidBody#step}.
     * @see EntityRigidBody#step
     * @see ConfigScreen
     */
    public enum Type {
        SIMPLE("config.rayon.option.air_resistance_type.simple", AirHelper::getSimpleForce),
        COMPLEX("config.rayon.option.air_resistance_type.complex", AirHelper::getComplexForce);

        final String name;
        final Function<EntityRigidBody, Vector3f> forceCalculation;

        Type(String name, Function<EntityRigidBody, Vector3f> forceCalculation) {
            this.name = name;
            this.forceCalculation = forceCalculation;
        }

        public String getName() {
            return name;
        }

        public Vector3f calculate(EntityRigidBody entity) {
            return forceCalculation.apply(entity);
        }
    }

    public static Vector3f getComplexForce(EntityRigidBody entity) {
        return new Vector3f();
    }

    /**
     * A simpler version of the above force calculations. It uses an calculated average
     * area from the {@link EntityRigidBody} object's AABB so it <i>should</i> slightly be faster.
     * @return a {@link Vector3f} containing the direction and amount of force (in newtons)
     * @see EntityRigidBody
     * @see AirHelper#getComplexForce
     */
    public static Vector3f getSimpleForce(EntityRigidBody entity) {
        return getForce(
                entity.getLinearVelocity(new Vector3f()),
                (float) Math.pow(entity.getBox().getAverageSideLength(), 2),
                entity.getDragCoefficient()
        );
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
        float k = (Config.INSTANCE.airDensity * dragCoefficient * area) / 2.0f;
        Vector3f force = VectorHelper.mul(velocity, k);
        force.scale(-velocity.lengthSquared());
        return force;
    }
}
