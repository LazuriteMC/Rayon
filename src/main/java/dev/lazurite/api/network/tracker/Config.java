package dev.lazurite.api.network.tracker;

import dev.lazurite.api.network.tracker.generic.GenericType;

import java.util.Properties;

/**
 * The main config handling class for not only reading and writing
 * from the file, but also being passed between the client and
 * the server.
 */
public class Config extends Properties {

    /**
     * Empty constructor :/
     */
    public Config() {

    }

    /**
     * Get a value from the config using a {@link Key} object.
     * @param key the key from the config
     * @param <T> the type of the key
     * @return the value returned from the key
     */
    public <T> T getValue(Key<T> key) {
        return key.getType().fromConfig(this, key.getName());
    }

    public <T> void setValue(Key<T> key, T value) {
        key.getType().toConfig(this, key.getName(), value);
    }

    /**
     * A class that represents a key in the config file.
     * Stores the name and the type of the key.
     * @param <T> the type of the key
     */
    public static class Key<T> {
        private final String name;
        private final GenericType<T> type;

        public Key(String key, GenericType<T> type) {
            this.name = key;
            this.type = type;
        }

        public String getName() {
            return this.name;
        }

        public GenericType<T> getType() {
            return this.type;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Key<?>) {
                Key<?> key = (Key<?>) obj;
                return getName().equals(key.getName()) && getType().equals(key.getType());
            }

            return false;
        }
    }
}
