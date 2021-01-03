package dev.lazurite.rayon.api.registry;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lazurite.rayon.physics.Rayon;

import java.io.InputStreamReader;
import java.util.Map;

public class PropertyRegistry {
    private static final Map<String, JsonObject> properties = Maps.newHashMap();

    static {
        PropertyRegistry.register(Rayon.MODID, "assets/rayon/rayon.properties.json");
    }

    public static void register(String modid, InputStreamReader reader) {
        properties.put(modid, (JsonObject) new JsonParser().parse(reader));
    }

    public static void register(String modid, String fileName) {
        register(modid, new InputStreamReader(PropertyRegistry.class.getResourceAsStream("/" + fileName)));
    }

    public static JsonArray get(String listName) {
        JsonArray out = new JsonArray();
        properties.forEach((modid, object) -> out.addAll(object.getAsJsonArray(listName)));
        return out;
    }
}
