package dev.lazurite.rayon.network.tracker;

import dev.lazurite.rayon.server.entity.NetworkSyncedEntity;
import dev.lazurite.rayon.network.tracker.generic.GenericType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.CompoundTag;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class EntityTrackerRegistry {
    private static final List<Entry<?>> entries = new LinkedList<>();

    public static <T> Entry<T> register(String key, GenericType<T> type, T fallback, Class<? extends NetworkSyncedEntity> classType) {
        Entry<T> entry = new Entry<>(key, type, classType, fallback);
        entries.add(entry);
        return entry;
    }

    public static <T> Entry<T> register(String key, GenericType<T> type, T fallback, Class<? extends NetworkSyncedEntity> classType, BiConsumer<NetworkSyncedEntity, T> consumer) {
        Entry<T> entry = new Entry<>(key, type, classType, fallback, consumer);
        entries.add(entry);
        return entry;
    }

    public static <T> void writeToTag(CompoundTag tag, Entry<T> entry, T value) {
        entry.getType().toTag(tag, entry.getKey(), value);
    }

    public static <T> T readFromTag(CompoundTag tag, Entry<T> entry) {
        return entry.getType().fromTag(tag, entry.getKey());
    }

    public static List<Entry<?>> getAll() {
        return new LinkedList<>(entries);
    }

    public static List<Entry<?>> getAll(Class<?> classType) {
        List<Entry<?>> out = new LinkedList<>();

        entries.forEach(entry -> {
            if (entry.getEntityType().isAssignableFrom(classType)) {
                out.add(entry);
            }
        });

        return out;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<Entry<T>> getAll(Class<?> entityType, GenericType<T> dataType) {
        List<Entry<T>> out = new LinkedList<>();

        entries.forEach(entry ->  {
            if (entry.getEntityType().isAssignableFrom(entityType) && entry.getType() == dataType) {
                out.add((Entry<T>) entry);
            }
        });

        return out;
    }

    public static class Entry<T> {
        private final String key;
        private final GenericType<T> type;
        private final T fallback;
        private final Class<? extends NetworkSyncedEntity> entityType;
        private final TrackedData<T> trackedData;
        private BiConsumer<NetworkSyncedEntity, T> consumer;

        public Entry(String key, GenericType<T> type, Class<? extends NetworkSyncedEntity> entityType, T fallback) {
            this.key = key;
            this.type = type;
            this.fallback = fallback;
            this.entityType = entityType;
            this.trackedData = DataTracker.registerData(entityType, type);
        }

        public Entry(String key, GenericType<T> type, Class<? extends NetworkSyncedEntity> entityType, T fallback, BiConsumer<NetworkSyncedEntity, T> consumer) {
            this(key, type, entityType, fallback);
            this.consumer = consumer;
        }

        public String getKey() {
            return this.key;
        }

        public GenericType<T> getType() {
            return this.type;
        }

        public Class<? extends NetworkSyncedEntity> getEntityType() {
            return this.entityType;
        }

        public T getFallback() {
            return this.fallback;
        }

        public BiConsumer<NetworkSyncedEntity, T> getConsumer() {
            return this.consumer;
        }

        public TrackedData<T> getTrackedData() {
            return this.trackedData;
        }
    }
}