package dev.lazurite.rayon.core.impl.physics.util.thread;

import dev.lazurite.rayon.core.impl.physics.PhysicsThread;

public interface ThreadStorage {
    void setPhysicsThread(PhysicsThread thread);
    PhysicsThread getPhysicsThread();
}
