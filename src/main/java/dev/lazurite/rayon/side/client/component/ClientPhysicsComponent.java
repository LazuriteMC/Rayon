package dev.lazurite.rayon.side.client.component;

import dev.lazurite.rayon.component.PhysicsComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class ClientPhysicsComponent extends PhysicsComponent {

    public ClientPhysicsComponent(Entity owner) {
        super(owner);
    }

    public void step(float delta) {
//        if (age > 2) {
//            physics.applyForce(AirHelper.getResistanceForce(physics.getLinearVelocity(), getValue(DRAG_COEFFICIENT)));
//        }
    }
}
