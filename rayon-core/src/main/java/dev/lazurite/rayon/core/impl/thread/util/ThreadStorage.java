package dev.lazurite.rayon.core.impl.thread.util;

import dev.lazurite.rayon.core.impl.thread.PhysicsThread;

public interface ThreadStorage {
    void setPhysicsThread(PhysicsThread thread);
    PhysicsThread getPhysicsThread();
}
