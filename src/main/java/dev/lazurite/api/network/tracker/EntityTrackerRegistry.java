package dev.lazurite.api.network.tracker;

import dev.lazurite.api.server.entity.NetworkSyncedEntity;
import dev.lazurite.api.network.tracker.generic.GenericType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.CompoundTag;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class EntityTrackerRegistry {
    private static final List<Entry<?>> entries = new LinkedList<>();

    public static <T> Entry<T> register(Config.Key<T> key, T fallback, Class<? extends NetworkSyncedEntity> classType) {
        Entry<T> entry = new Entry<>(key, fallback, classType);
        entries.add(entry);
        return entry;
    }

    public static <T> Entry<T> register(Config.Key<T> key, T fallback, Class<? extends NetworkSyncedEntity> classType, BiConsumer<NetworkSyncedEntity, T> consumer) {
        Entry<T> entry = new Entry<>(key, fallback,  classType, consumer);
        entries.add(entry);
        return entry;
    }

    public static <T> void writeToTag(CompoundTag tag, Entry<T> entry, T value) {
        entry.getKey().getType().toTag(tag, entry.getKey().getName(), value);
    }

    public static <T> T readFromTag(CompoundTag tag, Entry<T> entry) {
        return entry.getKey().getType().fromTag(tag, entry.getKey().getName());
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
            if (entry.getEntityType().isAssignableFrom(entityType) && entry.getKey().getType() == dataType) {
                out.add((Entry<T>) entry);
            }
        });

        return out;
    }

    public static class Entry<T> {
        private final Config.Key<T> key;
        private final T fallback;
        private final Class<? extends NetworkSyncedEntity> entityType;
        private final TrackedData<T> trackedData;
        private BiConsumer<NetworkSyncedEntity, T> consumer;

        public Entry(Config.Key<T> key, T fallback, Class<? extends NetworkSyncedEntity> entityType) {
            this.key = key;
            this.fallback = fallback;
            this.entityType = entityType;
            this.trackedData = DataTracker.registerData(entityType, key.getType());
        }

        public Entry(Config.Key<T> key, T fallback, Class<? extends NetworkSyncedEntity> entityType, BiConsumer<NetworkSyncedEntity, T> consumer) {
            this(key, fallback, entityType);
            this.consumer = consumer;
        }

        public Config.Key<T> getKey() {
            return this.key;
        }

        public T getFallback() {
            return this.fallback;
        }

        public TrackedData<T> getTrackedData() {
            return this.trackedData;
        }

        public Class<? extends NetworkSyncedEntity> getEntityType() {
            return this.entityType;
        }

        public BiConsumer<NetworkSyncedEntity, T> getConsumer() {
            return this.consumer;
        }
    }
}