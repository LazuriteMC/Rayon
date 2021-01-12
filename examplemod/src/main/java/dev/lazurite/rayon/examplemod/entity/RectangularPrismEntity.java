package dev.lazurite.rayon.examplemod.entity;

import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class RectangularPrismEntity extends Entity {
    public RectangularPrismEntity(EntityType<?> type, World world) {
        super(type, world);
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
    public void kill() {
        super.kill();
        this.dropItem(Items.FEATHER);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return RayonSpawnS2CPacket.get(this);
    }
}
