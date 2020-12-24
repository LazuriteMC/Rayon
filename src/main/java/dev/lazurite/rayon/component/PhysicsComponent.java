package dev.lazurite.rayon.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;

public interface PhysicsComponent extends ComponentV3, CommonTickingComponent, AutoSyncedComponent {
    void step(float delta);
}
