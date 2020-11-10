package dev.lazurite.api.server.entity;

import dev.lazurite.api.util.TickTimer;
import dev.lazurite.api.network.tracker.Config;
import dev.lazurite.api.network.tracker.EntityTrackerRegistry;
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

    public <T> void setValue (Config.Key<T> key, T value) {
        for (EntityTrackerRegistry.Entry<T> entry : EntityTrackerRegistry.getAll(getClass(), key.getType())) {
            if (entry.getKey().equals(key)) {
                setValue(entry, value);
            }
        }
    }
    public <T> T getValue(EntityTrackerRegistry.Entry<T> entry) {
        T data = getDataTracker().get(entry.getTrackedData());

        if (data == null) {
            return entry.getFallback();
        }

        return data;
    }

    public <T> T getValue(Config.Key<T> key) {
        for (EntityTrackerRegistry.Entry<T> entry : EntityTrackerRegistry.getAll(getClass(), key.getType())) {
            if (entry.getKey().equals(key)) {
                return getValue(entry);
            }
        }

        return null;
    }

    protected abstract void sendNetworkPacket();

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void writeCustomDataToTag(CompoundTag tag) {
        EntityTrackerRegistry.getAll(getClass()).forEach(entry -> EntityTrackerRegistry.writeToTag(tag, (EntityTrackerRegistry.Entry) entry, getValue(entry)));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void readCustomDataFromTag(CompoundTag tag) {
        EntityTrackerRegistry.getAll(getClass()).forEach(entry -> setValue((EntityTrackerRegistry.Entry) entry, EntityTrackerRegistry.readFromTag(tag, entry)));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void initDataTracker() {
        EntityTrackerRegistry.getAll(getClass()).forEach(entry -> getDataTracker().startTracking(((EntityTrackerRegistry.Entry) entry).getTrackedData(), entry.getFallback()));
    }
}
