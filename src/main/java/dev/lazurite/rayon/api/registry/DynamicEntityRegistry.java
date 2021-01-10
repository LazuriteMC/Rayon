package dev.lazurite.rayon.api.registry;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.api.shape.factory.EntityShapeFactory;
import dev.lazurite.rayon.Rayon;
import net.minecraft.entity.Entity;

import java.util.List;

/**
 * The registry used for registering custom entities as dynamic entities. Register
 * using the {@link DynamicEntityRegistry#register} method.
 * @since 1.0.0
 * @see Rayon#registerEntityComponentFactories 
 */
public class DynamicEntityRegistry {
    public static final DynamicEntityRegistry INSTANCE = new DynamicEntityRegistry();

    private final List<Entry<? extends Entity>> entities;

    private DynamicEntityRegistry() {
        entities = Lists.newArrayList();
    }

    public <E extends Entity> void register(Class<E> entity, EntityShapeFactory shapeFactory, float mass, float dragCoefficient) {
        entities.add(new Entry<>(entity, shapeFactory, mass, dragCoefficient));
    }

    public List<Entry<? extends Entity>> get() {
        return Lists.newArrayList(entities);
    }

    /**
     * A container to house all the data for each registered entity.
     * @param <E> the specific type of {@link Entity}
     * @see DynamicEntityRegistry#register
     */
    public static class Entry<E extends Entity> {
        private final Class<E> entity;
        private final EntityShapeFactory shapeFactory;
        private final float mass;
        private final float dragCoefficient;

        public Entry(Class<E> entity, EntityShapeFactory shapeFactory, float mass, float dragCoefficient) {
            this.entity = entity;
            this.shapeFactory = shapeFactory;
            this.mass = mass;
            this.dragCoefficient = dragCoefficient;
        }

        public Class<E> getEntity() {
            return this.entity;
        }

        public EntityShapeFactory getShapeFactory() {
            return this.shapeFactory;
        }

        public float getMass() {
            return this.mass;
        }

        public float getDragCoefficient() {
            return this.dragCoefficient;
        }
    }
}
