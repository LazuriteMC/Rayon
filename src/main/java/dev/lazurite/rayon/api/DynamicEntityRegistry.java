package dev.lazurite.rayon.api;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;

import java.util.List;

public class DynamicEntityRegistry {
    private static final List<Class<? extends Entity>> entities = Lists.newArrayList();

    public static void register(Class<? extends Entity> entity) {
        entities.add(entity);
    }

    public static List<Class<? extends Entity>> get() {
        return Lists.newArrayList(entities);
    }
}
