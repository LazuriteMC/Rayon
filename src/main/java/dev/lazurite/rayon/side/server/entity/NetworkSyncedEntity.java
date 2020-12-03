package dev.lazurite.rayon.side.server.entity;

import dev.lazurite.rayon.thimble.tracker.EntityTrackerRegistry;
import dev.lazurite.rayon.util.TickTimer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

/**
 * A basic type of entity which syncs information across the network.
 * @author Ethan Johnson
 */
public abstract class NetworkSyncedEntity extends Entity {

    /** A timer which controls how often packets are sent */
    private final TickTimer timer;

    /**
     * Allows the user to specify a sync rate (which is given to {@link NetworkSyncedEntity#timer}).
     * @param type the entity type
     * @param world the world
     * @param syncRate rate at which packets are sent
     */
    public NetworkSyncedEntity(EntityType<?> type, World world, int syncRate) {
        super(type, world);
        this.timer = new TickTimer(syncRate);
    }

    /**
     * Controls the release of synced data packets.
     */
    @Override
    public void tick() {
        super.tick();

        if (timer.tick()) {
            sendNetworkPacket();
        }
    }

    /**
     * Used for setting a tracked value.
     * @param entry the tracked data entry
     * @param value the value to be set
     * @param <T> the type of the tracked data entry
     */
    public <T> void setValue(EntityTrackerRegistry.Entry<T> entry, T value) {
        this.getDataTracker().set(entry.getTrackedData(), value);

        if (entry.getConsumer() != null) {
            entry.getConsumer().accept(this, value);
        }
    }

    /**
     * Used for getting a tracked value.
     * @param entry the tracked data entry
     * @param <T> the type of the tracked data entry
     * @return the value of the entry
     */
    public final <T> T getValue(EntityTrackerRegistry.Entry<T> entry) {
        T data = getDataTracker().get(entry.getTrackedData());

        if (data == null) {
            return entry.getFallback();
        }

        return data;
    }

    /**
     * All subsequent entities must implements this. It is where the user
     * can define which packets should be sent or any other desired behavior.
     */
    protected abstract void sendNetworkPacket();

    /**
     * Facilitates writing all tracked data to a {@link CompoundTag} during world save procedures.
     * @param tag the tag to save to
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final void writeCustomDataToTag(CompoundTag tag) {
        EntityTrackerRegistry.getAll(getClass()).forEach(entry -> EntityTrackerRegistry.writeToTag(tag, (EntityTrackerRegistry.Entry) entry, getValue(entry)));
    }

    /**
     * Facilitates reading all tracked data from a {@link CompoundTag} during world load procedures.
     * @param tag the tag to read from
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final void readCustomDataFromTag(CompoundTag tag) {
        EntityTrackerRegistry.getAll(getClass()).forEach(entry -> setValue((EntityTrackerRegistry.Entry) entry, EntityTrackerRegistry.readFromTag(tag, entry)));
    }

    /**
     * Facilitates loading all tracked data entries into Minecraft's {@link net.minecraft.entity.data.DataTracker} system.
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final void initDataTracker() {
        EntityTrackerRegistry.getAll(getClass()).forEach(entry -> getDataTracker().startTracking(((EntityTrackerRegistry.Entry) entry).getTrackedData(), entry.getFallback()));
    }
}
