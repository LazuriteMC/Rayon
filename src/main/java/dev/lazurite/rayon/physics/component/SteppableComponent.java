package dev.lazurite.rayon.physics.component;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.component.entity.PhysicsEntityComponent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface SteppableComponent {
    static void stepAll(World world, float delta) {
        Rayon.PHYSICS_WORLD.get(world).step(delta);

        for (Entity entity : world.getEntities()) {
            PhysicsEntityComponent component = PhysicsEntityComponent.get(entity);

            if (component != null) {
                Rayon.PHYSICS_ENTITY.get(entity).step(delta);
            }
        }

    }

    void step(float delta);
}
