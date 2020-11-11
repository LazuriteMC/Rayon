package dev.lazurite.api.physics.server.entity;

import dev.lazurite.api.physics.network.tracker.EntityTrackerRegistry;
import dev.lazurite.api.physics.util.TickTimer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

public abstract class NetworkSyncedEntity extends Entity {
    private final TickTimer timer;

    public NetworkSyncedEntity(EntityType<?> type, World world, int syncRate) {
        super(type, world);
        this.timer = new TickTimer(syncRate);
    }

    public NetworkSyncedEntity(EntityType<?> type, World world) {
        this(type, world, 5);
    }

    @Override
    public void tick() {
        super.tick();

        if (timer.tick()) {
            sendNetworkPacket();
        }
    }

    public <T> void setValue(EntityTrackerRegistry.Entry<T> entry, T value) {
        this.getDataTracker().set(entry.getTrackedData(), value);

        if (entry.getConsumer() != null) {
            entry.getConsumer().accept(this, value);
        }
    }

    public final <T> T getValue(EntityTrackerRegistry.Entry<T> entry) {
        T data = getDataTracker().get(entry.getTrackedData());

        if (data == null) {
            return entry.getFallback();
        }

        return data;
    }

    protected abstract void sendNetworkPacket();

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final void writeCustomDataToTag(CompoundTag tag) {
        EntityTrackerRegistry.getAll(getClass()).forEach(entry -> EntityTrackerRegistry.writeToTag(tag, (EntityTrackerRegistry.Entry) entry, getValue(entry)));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final void readCustomDataFromTag(CompoundTag tag) {
        EntityTrackerRegistry.getAll(getClass()).forEach(entry -> setValue((EntityTrackerRegistry.Entry) entry, EntityTrackerRegistry.readFromTag(tag, entry)));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final void initDataTracker() {
        EntityTrackerRegistry.getAll(getClass()).forEach(entry -> getDataTracker().startTracking(((EntityTrackerRegistry.Entry) entry).getTrackedData(), entry.getFallback()));
    }
}
