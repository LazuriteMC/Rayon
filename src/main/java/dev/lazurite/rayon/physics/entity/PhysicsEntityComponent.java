package dev.lazurite.rayon.physics.entity;

import dev.lazurite.rayon.physics.Rayon;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.Level;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.NoSuchElementException;

public interface PhysicsEntityComponent extends ComponentV3, CommonTickingComponent, AutoSyncedComponent {
    static PhysicsEntityComponent get(Entity entity) {
        try {
            return Rayon.PHYSICS_ENTITY.get(entity);
        } catch (NoSuchElementException e) {
            Rayon.LOGGER.log(Level.ERROR, "Entity is not registered.");
            return null;
        }
    }

    void step(float delta);

    void setOrientation(Quat4f orientation);
    void setPosition(Vector3f position);
    void setLinearVelocity(Vector3f linearVelocity);
    void setAngularVelocity(Vector3f angularVelocity);

    Quat4f getOrientation();
    Vector3f getPosition();
    Vector3f getLinearVelocity();
    Vector3f getAngularVelocity();
}
