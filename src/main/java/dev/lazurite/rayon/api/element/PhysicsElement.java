package dev.lazurite.rayon.api.element;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.impl.element.ElementRigidBody;
import dev.lazurite.rayon.impl.element.interpolate.Frame;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

public interface PhysicsElement {
    void step(MinecraftSpace space);

    ElementRigidBody getRigidBody();

    @Environment(EnvType.CLIENT)
    default Vector3f getPhysicsLocation(Vector3f store, float tickDelta) {
        Frame frame = ((Frame.Storage) this).getFrame();

        if (frame != null) {
            store.set(frame.getLocation(new Vector3f(),  tickDelta));
        }

        return store;
    }

    @Environment(EnvType.CLIENT)
    default Quaternion getPhysicsRotation(Quaternion store, float tickDelta) {
        Frame frame = ((Frame.Storage) this).getFrame();

        if (frame != null) {
            store.set(frame.getRotation(new Quaternion(), tickDelta));
        }

        return store;
    }

    default Entity asEntity() {
        return (Entity) this;
    }
}
