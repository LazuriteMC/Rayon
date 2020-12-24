package dev.lazurite.rayon.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class DynamicPhysicsComponent implements PhysicsComponent, AutoSyncedComponent {
    private final Entity entity;

    public DynamicPhysicsComponent(@NotNull Entity entity) {
        this.entity = entity;
    }

    @Override
    public void tick() {
        entity.setVelocity(0, 0.1, 0);
    }

    @Override
    public void step(float delta) {

    }

    @Override
    public void readFromNbt(CompoundTag compoundTag) {
    }

    @Override
    public void writeToNbt(CompoundTag compoundTag) {
    }
}
