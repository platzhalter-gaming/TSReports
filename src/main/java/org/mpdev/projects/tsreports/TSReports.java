package org.mpdev.projects.tsreports;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.mpdev.projects.tsreports.commands.AdminCommand;
import org.mpdev.projects.tsreports.commands.ReportCommand;
import org.mpdev.projects.tsreports.dependency.Dependency;
import org.mpdev.projects.tsreports.dependency.DependencyManager;
import org.mpdev.projects.tsreports.inventory.InventoryController;
import org.mpdev.projects.tsreports.listeners.ConnectionListener;
import org.mpdev.projects.tsreports.listeners.ReportListener;
import org.mpdev.projects.tsreports.managers.ConfigManager;
import org.mpdev.projects.tsreports.managers.StorageManager;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.utils.PluginHelp;
import org.mpdev.projects.tsreports.utils.UpdateChecker;
import org.mpdev.projects.tsreports.utils.Utils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;

public final class TSReports extends Plugin {

    private static TSReports instance;

    private ConfigManager configManager;
    private StorageManager storageManager;
    private DependencyManager dependencyManager;
    private InventoryController inventoryController;
    private LuckPerms api;

    private String[] adminCommands;

    private Map<String, OfflinePlayer> offlinePlayers;
    private Map<String, Boolean> commands;

    public static TSReports getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);
        this.dependencyManager = new DependencyManager();
        Path pluginsFolder = getProxy().getPluginsFolder().toPath();
        if (getConfig().getBoolean("protocolize")) {
            dependencyManager.downloadDependency(Dependency.PROTOCOLIZE_BUNGEECORD, pluginsFolder.resolve(Dependency.PROTOCOLIZE_BUNGEECORD.getFileName()));
        }
        this.storageManager = new StorageManager();
        this.offlinePlayers = storageManager.getAllOfflinePlayers();

        Utils.setupMetrics();
        new UpdateChecker(this).start();

        this.adminCommands = getConfig().getStringList("adminCommands").toArray(new String[0]);
        String[] reportCommands = getConfig().getStringList("reportCommands").toArray(new String[0]);

        getProxy().getPluginManager().registerListener(this, new ConnectionListener(this));
        getProxy().getPluginManager().registerListener(this, new ReportListener(this));
        getProxy().getPluginManager().registerCommand(this, new ReportCommand(this, reportCommands[0], null, reportCommands));
        getProxy().getPluginManager().registerCommand(this, new AdminCommand(this, adminCommands[0], null, adminCommands));

        this.commands = PluginHelp.setupCommands();
        this.inventoryController = new InventoryController();

        if (Utils.isPluginEnabled("LuckPerms")) {
            try {
                api = LuckPermsProvider.get();
            } catch (IllegalStateException e) {
                getLogger().log(Level.SEVERE, "There has been a problem with LuckPerms: " + e.getMessage(), e);
            }
        }
    }

    public String[] getAdminCommands() {
        return adminCommands;
    }

    public LuckPerms getApi() {
        return api;
    }

    public Map<String, Boolean> getCommands() {
        return commands;
    }

    public void setCommands(Map<String, Boolean> commands) {
        this.commands = commands;
    }

    public InventoryController getInventoryController() {
        return inventoryController;
    }

    public Map<String, OfflinePlayer> getOfflinePlayers() {
        return offlinePlayers;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
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

}
