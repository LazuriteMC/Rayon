package dev.lazurite.rayon.server.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

public class TestEntity extends PhysicsEntity {
    public TestEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket();
    }
}
