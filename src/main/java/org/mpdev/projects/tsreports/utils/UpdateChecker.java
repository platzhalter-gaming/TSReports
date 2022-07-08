package org.mpdev.projects.tsreports.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.md_5.bungee.config.Configuration;
import org.mpdev.projects.tsreports.TSReports;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class UpdateChecker {
    private final TSReports plugin;
    private final Configuration config;
    private final String currentVersion;
    private String latestVersion;

    public UpdateChecker(TSReports plugin) {
        this.plugin = plugin;
        this.config = TSReports.getInstance().getConfigManager().getConfig();
        this.currentVersion = plugin.getDescription().getName();
    }

    public void start() {
        if (config.getBoolean("updateChecker", true)){
            plugin.getProxy().getScheduler().schedule(plugin, this::check, 0, 12, TimeUnit.HOURS);
        }
    }

    public void check() {
        plugin.getLogger().info("Checking for new updates...");
        try {
            URL url = new URL("https://api.spiget.org/v2/resources/100163/versions/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());

            JsonElement parse = new JsonParser().parse(reader);
            if (parse.isJsonObject()) {
                latestVersion = parse.getAsJsonObject().get("name").getAsString();
            }

            if (currentVersion.equals(latestVersion)) {
                plugin.getLogger().info("The plugin is up to date.");
            } else {
                plugin.getLogger().info("New version found: " + latestVersion);
                plugin.getLogger().info("Download here: https://www.spigotmc.org/resources/100163/");
            }

            reader.close();
        } catch (IOException ex) {
            plugin.getLogger().warning("There was a problem searching for updates!");
        }
    }

}
