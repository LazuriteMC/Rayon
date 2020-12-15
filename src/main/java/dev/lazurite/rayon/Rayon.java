package dev.lazurite.rayon;

import dev.lazurite.rayon.physics.composition.DynPhysicsComposition;
import dev.lazurite.thimble.Thimble;
import dev.lazurite.thimble.composition.Composition;
import net.minecraft.entity.Entity;

public class Rayon {
    public static DynPhysicsComposition getPhysics(Entity entity) {
        for (Composition composition : Thimble.getStitches(entity)) {
            if (composition instanceof DynPhysicsComposition) {
                return (DynPhysicsComposition) composition;
            }
        }

        return null;
    }

    public static boolean hasPhysics(Entity entity) {
        return getPhysics(entity) != null;
    }
}
