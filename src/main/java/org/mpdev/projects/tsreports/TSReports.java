package org.mpdev.projects.tsreports;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.mpdev.projects.tsreports.commands.AdminCommand;
import org.mpdev.projects.tsreports.commands.ReportCommand;
import org.mpdev.projects.tsreports.inventory.InventoryController;
import org.mpdev.projects.tsreports.listeners.ConnectionListener;
import org.mpdev.projects.tsreports.listeners.ReportListener;
import org.mpdev.projects.tsreports.managers.ConfigManager;
import org.mpdev.projects.tsreports.managers.StorageManager;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.utils.UpdateChecker;
import org.mpdev.projects.tsreports.utils.Utils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public final class TSReports extends Plugin {

    private static TSReports instance;

    private ConfigManager configManager;
    private StorageManager storageManager;
    private InventoryController inventoryController;

    private Map<String, OfflinePlayer> offlinePlayers;
    private List<String> blacklisted;

    private boolean protocolize;
    private String[] adminCommandAliases;

    public static TSReports getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);
        this.storageManager = new StorageManager(this);
        this.offlinePlayers = storageManager.getAllPlayers();
        this.blacklisted = getConfig().getStringList("blacklisted");
        this.protocolize = getConfig().getBoolean("protocolize") && Utils.isPluginEnabled("Protocolize");

        if (protocolize) {
            getLogger().info("Protocolize has been found. Hooking in..");
        } else {
            getLogger().warning("# You can ignore this warning #");
            getLogger().warning("Protocolize hasn't been found. Either its disabled in config or not installed!");
            getLogger().warning("# You can ignore this warning #");
        }

        Utils.setupMetrics();
        new UpdateChecker(this).start();

        getProxy().getPluginManager().registerListener(this, new ConnectionListener(this));
        getProxy().getPluginManager().registerListener(this, new ReportListener(this));
        String[] reportCommandAliases = (getConfig().getStringList("reportCommandAliases")).toArray(new String[0]);
        getProxy().getPluginManager().registerCommand(this, new ReportCommand(this, reportCommandAliases[0], null, reportCommandAliases));
        this.adminCommandAliases = (getConfig().getStringList("adminCommandAliases")).toArray(new String[0]);
        getProxy().getPluginManager().registerCommand(this, new AdminCommand(this, adminCommandAliases[0], null, adminCommandAliases));

        this.inventoryController = new InventoryController();
    }

    public String[] getAdminCommandAliases() {
        return adminCommandAliases;
    }

    public InventoryController getInventoryController() {
        return inventoryController;
    }

    public Map<String, OfflinePlayer> getOfflinePlayers() {
        return offlinePlayers;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public InputStream getResourceStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    public Configuration getConfig() {
        return getConfigManager().getConfig();
    }

    public void debug(String message) {
        if (!getConfig().getBoolean("debug", false)) return;
        getLogger().info(message);
    }

    public boolean isProtocolize() {
        return protocolize;
    }

    public List<String> getBlacklisted() {
        return blacklisted;
    }
}
