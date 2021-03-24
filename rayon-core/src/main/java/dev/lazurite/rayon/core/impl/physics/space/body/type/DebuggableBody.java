package dev.lazurite.rayon.core.impl.physics.space.body.type;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.util.debug.DebugManager;
import dev.lazurite.rayon.core.impl.util.debug.DebugLayer;

/**
 * Any {@link PhysicsRigidBody} with this assigned will show up
 * when rending rigid bodies using the {@link DebugManager}.
 *
 * @see DebugManager
 * @see DebugLayer
 */
public interface DebuggableBody {
    default Vector3f getOutlineColor() {
        return new Vector3f(1.0f, 1.0f, 1.0f);
    }

    default float getOutlineAlpha() {
        return 0.5f;
    }

    default DebugLayer getDebugLayer() {
        return DebugLayer.BLOCK;
    }
}
