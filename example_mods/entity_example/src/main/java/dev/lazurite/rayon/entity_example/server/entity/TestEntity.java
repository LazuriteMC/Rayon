package dev.lazurite.rayon.entity_example.server.entity;

import com.bulletphysics.collision.shapes.BoxShape;
import dev.lazurite.rayon.server.entity.PhysicsEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;

public class TestEntity extends PhysicsEntity {
    public TestEntity(EntityType<?> type, World world) {
        super(type, world, new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f)));
        this.setValue(MASS, 1.5f);
        this.setValue(DRAG_COEFFICIENT, 0.005f);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void step(float delta) {
        super.step(delta);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
