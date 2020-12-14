package dev.lazurite.rayon;

import dev.lazurite.rayon.composition.PhysicsComposition;
import dev.lazurite.thimble.Thimble;
import dev.lazurite.thimble.composition.Composition;
import net.minecraft.entity.Entity;

public class Rayon {
    public static PhysicsComposition getPhysics(Entity entity) {
        for (Composition composition : Thimble.getStitches(entity)) {
            if (composition instanceof PhysicsComposition) {
                return (PhysicsComposition) composition;
            }
        }

        return null;
    }
}
