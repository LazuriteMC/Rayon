package dev.lazurite.rayon.api.registry;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.api.shape.factory.EntityShapeFactory;
import dev.lazurite.rayon.Rayon;
import net.minecraft.entity.Entity;

import java.util.List;

/**
 * The registry used for registering custom entities as dynamic entities. Register
 * using any one of the {@link DynamicEntityRegistry#register} methods.
 * @since 1.0.0
 * @see Rayon#registerEntityComponentFactories 
 */
public final class DynamicEntityRegistry {
    public static final DynamicEntityRegistry INSTANCE = new DynamicEntityRegistry();

    private final List<Entry<? extends Entity>> entities = Lists.newArrayList();

    private DynamicEntityRegistry() {
    }

    /**
     * Registers an entity with Rayon.
     *
     * @param entity the entity class to register
     * @param shapeFactory used later on to give the entity a shape in the dynamics world
     * @param mass the mass of the entity
     * @param dragCoefficient the drag coefficient of the entity for air resistance simulation
     * @param <E> the type of entity class to register
     */
    public <E extends Entity> void register(Class<E> entity, EntityShapeFactory shapeFactory, float mass, float dragCoefficient) {
        entities.add(new Entry<>(entity, shapeFactory, mass, dragCoefficient));
    }

    /**
     * Register an entity Rayon. Drag coefficient defaults to 0.05
     *
     * @param entity the entity class to register
     * @param shapeFactory used later on to give the entity a shape in the dynamics world
     * @param mass the mass of the entity
     * @param <E> the type of entity class to register
     */
    public <E extends Entity> void register(Class<E> entity, EntityShapeFactory shapeFactory, float mass) {
        entities.add(new Entry<>(entity, shapeFactory, mass, 0.05f));
    }

    /**
     * Gets the list of registered entities.
     *
     * @return a new list containing registered entities
     * @see Rayon#registerEntityComponentFactories 
     */
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
