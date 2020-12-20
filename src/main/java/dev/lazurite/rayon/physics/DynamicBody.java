package dev.lazurite.rayon.physics;

import dev.lazurite.rayon.physics.composition.DynamicBodyComposition;

public interface DynamicBody {
    DynamicBodyComposition getDynamicBody();
    boolean hasDynamicBody();
    boolean belongsToClient();
}
