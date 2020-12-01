package dev.lazurite.api.physics.example.server.entity;

import dev.lazurite.rayon.server.entity.PhysicsEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class TestEntity extends PhysicsEntity {
    public TestEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }
}
