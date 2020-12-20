package dev.lazurite.rayon.physics.helper;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lazurite.rayon.init.ServerInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.InputStreamReader;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class PropertyHelper {
    private static final Map<String, JsonObject> properties = Maps.newHashMap();

    static {
        PropertyHelper.add(ServerInitializer.MODID, "assets/rayon/physics.properties.json");
    }

    public static void add(String modid, String fileName) {
        add(modid, new InputStreamReader(PropertyHelper.class.getResourceAsStream("/" + fileName)));
    }

    public static void add(String modid, InputStreamReader reader) {
        properties.put(modid, (JsonObject) new JsonParser().parse(reader));
    }

    public static JsonArray get(String listName) {
        JsonArray out = new JsonArray();
        properties.forEach((modid, object) -> out.addAll(object.getAsJsonArray(listName)));
        return out;
    }
}
