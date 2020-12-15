package dev.lazurite.rayon.physics.composition;

import dev.lazurite.rayon.side.server.ServerInitializer;
import dev.lazurite.thimble.composition.Composition;
import dev.lazurite.thimble.synchronizer.Synchronizer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class StaticPhysicsComposition extends Composition {
    public static final Identifier IDENTIFIER = new Identifier(ServerInitializer.MODID, "dynamic_physics");

    public StaticPhysicsComposition(Synchronizer synchronizer) {
        super(synchronizer);
    }

    @Override
    public void onTick(Entity entity) {

    }

    @Override
    public boolean onInteract(PlayerEntity playerEntity, Hand hand) {
        return false;
    }

    @Override
    public void onRemove() {

    }

    @Override
    public void initSynchronizer() {

    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }
}
