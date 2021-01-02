package dev.lazurite.rayon.api.registry;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.api.shape.EntityShape;
import dev.lazurite.rayon.api.shape.factory.EntityShapeFactory;
import net.minecraft.entity.Entity;

import java.util.List;

public class DynamicEntityRegistry {
    public static final DynamicEntityRegistry INSTANCE = new DynamicEntityRegistry();

    private final List<Entry<? extends Entity, ? extends EntityShape>> entities;

    private DynamicEntityRegistry() {
        entities = Lists.newArrayList();
    }

    public <E extends Entity, S extends EntityShape> void register(Class<E> entity, EntityShapeFactory<S> shape, float mass) {
        entities.add(new Entry<>(entity, shape, mass));
    }

    public List<Entry<? extends Entity, ? extends EntityShape>> get() {
        return Lists.newArrayList(entities);
    }

    public static class Entry<E extends Entity, S extends EntityShape> {
        private final Class<E> entity;
        private final EntityShapeFactory<S> shapeFactory;
        private final float mass;

        public Entry(Class<E> entity, EntityShapeFactory<S> shapeFactory, float mass) {
            this.entity = entity;
            this.shapeFactory = shapeFactory;
            this.mass = mass;
        }

        public Class<? extends Entity> getEntity() {
            return this.entity;
        }

        public EntityShapeFactory<S> getShapeFactory() {
            return this.shapeFactory;
        }

        public float getMass() {
            return this.mass;
        }
    }
}
