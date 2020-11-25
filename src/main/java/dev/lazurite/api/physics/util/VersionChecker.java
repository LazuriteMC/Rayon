package dev.lazurite.api.physics.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Used for checking the mod version against lazurite.dev
 * @author Ethan Johnson
 */
@Environment(EnvType.CLIENT)
public class VersionChecker implements Runnable {
    /** The message header, surrounded by square brackets */
    private static final String header = "{\"translate\": \"version_checker.header\", \"clickEvent\": {\"action\": \"open_url\", \"value\": \"%s\"}, \"color\": \"#616ad6\"}";

    /** The message body */
    private static final String message = "{\"translate\": \"version_checker.message\", \"clickEvent\": {\"action\": \"open_url\", \"value\": \"%s\"}, \"color\": \"white\"}";

    private final String modid;
    private final String version;
    private final String url;
    private String latestVersion;

    /**
     * The main constructor for a new {@link VersionChecker} object.
     * @param modid the mod id
     * @param version the current mod version installed
     * @param url the url for the mod's download page
     */
    public VersionChecker(String modid, String version, String url) {
        this.modid = modid;
        this.version = version;
        this.url = url;
    }

    /**
     * This method runs on a separate thread during game initialization. It
     * fetches the latest mod version from lazurite.dev and stores it.
     */
    @Override
    public void run() {
        InputStream in;

        try {
            in = new URL("https://raw.githubusercontent.com/LazuriteMC/lazuritemc.github.io/master/versions.json").openStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JsonObject version_info = ((JsonObject) new JsonParser().parse(new InputStreamReader(in))).getAsJsonObject(modid);
        latestVersion = version_info.get("version").getAsString();
    }

    /**
     * Sends a message to the player if their mod is out of date.
     */
    public void sendPlayerMessage() {
        if (!isLatestVersion()) {
            PlayerEntity player = MinecraftClient.getInstance().player;

            if (player != null) {
                player.sendMessage(Text.Serializer.fromJson(String.format(header, url)).append(Text.Serializer.fromJson(String.format(message, url))), false);
            }
        }
    }

    /**
     * Gets whether the mod is up to date.
     * @return Whether or not the current version and the latest version are equal
     */
    public boolean isLatestVersion() {
        return latestVersion.equals(version);
    }

    /**
     * Gets the latest version of the mod.
     * @return the latest version of the mod
     */
    public String getLatestVersion() {
        return latestVersion;
    }

    /**
     * Creates a new {@link VersionChecker}, runs the fetcher,
     * and returns the newly created object.
     * @param modid the mod id
     * @param version the current mod version
     * @param url the mod download url
     * @return the newly created {@link VersionChecker}
     */
    public static VersionChecker getVersion(String modid, String version, String url) {
        VersionChecker out = new VersionChecker(modid, version, url);
        Thread versionCheckThread = new Thread(out, "Version Check");
        versionCheckThread.start();
        return out;
    }
}