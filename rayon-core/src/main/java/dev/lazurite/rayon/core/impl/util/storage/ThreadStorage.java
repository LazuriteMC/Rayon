package dev.lazurite.rayon.core.impl.util.storage;

import dev.lazurite.rayon.core.impl.physics.PhysicsThread;

public interface ThreadStorage {
    void setPhysicsThread(PhysicsThread thread);
    PhysicsThread getPhysicsThread();
}
