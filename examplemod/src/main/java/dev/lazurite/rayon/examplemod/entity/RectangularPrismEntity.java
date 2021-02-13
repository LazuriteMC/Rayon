package dev.lazurite.rayon.examplemod.entity;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.element.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.space.MinecraftSpace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

public class RectangularPrismEntity extends Entity implements PhysicsElement {
    private final ElementRigidBody rigidBody;

    public RectangularPrismEntity(EntityType<?> type, World world) {
        super(type, world);
        this.rigidBody = new ElementRigidBody(this);
    }

    @Override
    public void step(MinecraftSpace space) {
        rigidBody.applyCentralForce(new Vector3f(0, 0.1f, 0));
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {

    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {

    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public ElementRigidBody getRigidBody() {
        return this.rigidBody;
    }
}
