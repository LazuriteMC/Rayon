package dev.lazurite.rayon.component;

import dev.lazurite.rayon.Rayon;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.Level;

import java.util.NoSuchElementException;

public interface PhysicsComponent extends ComponentV3, CommonTickingComponent, AutoSyncedComponent {
    static PhysicsComponent get(Entity entity) {
        try {
            return Rayon.PHYSICS.get(entity);
        } catch (NoSuchElementException e) {
            Rayon.LOGGER.log(Level.ERROR, "Entity is not registered.");
            return null;
        }
    }

    void step(float delta);
}
