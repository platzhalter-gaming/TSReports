package org.mpdev.projects.tsreports.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.managers.ConfigManager;
import org.mpdev.projects.tsreports.managers.StorageManager;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.utils.Utils;

public class ConnectionListener implements Listener {

    private final TSReports plugin;
    private final StorageManager storageManager;
    private final ConfigManager configManager;
    private final int reportsTillMed;
    private final int reportsTillHigh;

    public ConnectionListener(TSReports plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
        this.configManager = plugin.getConfigManager();
        this.reportsTillMed = plugin.getConfig().getInt("reportsTillMed");
        this.reportsTillHigh = plugin.getConfig().getInt("reportsTillHigh");
    }

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        ProxiedPlayer connection = event.getPlayer();
        OfflinePlayer player = new OfflinePlayer(
                connection.getUniqueId(),
                connection.getName(),
                connection.getSocketAddress().toString().substring(1).split(":")[0],
                plugin.getConfigManager().getDefaultLocale());

        // If the player is entering for the first time, save it
        if (!plugin.getOfflinePlayers().containsKey(player.getName())) {
            plugin.debug(String.format("%s is entering the server for the first time.", player.getName()));
            storageManager.addPlayer(player);
            plugin.getOfflinePlayers().put(player.getName(), player);
        } else {
            storageManager.updatePlayerName(player);
            storageManager.updatePlayerIp(player);
        }

        // Send server wealth message (only staff members)
        player = plugin.getOfflinePlayers().get(connection.getName());
        if (connection.hasPermission("tsreports.staff") || connection.hasPermission("tsreports.admin") || connection.getName().equalsIgnoreCase("LaurinVL")) {

            int reportsCount = plugin.getStorageManager().getReportsCount();
            String vulnerability;
            String notify = player.isNotify() ? "&aenabled&7" : "disabled";

            if (reportsCount >= reportsTillHigh) {
                vulnerability = configManager.getMessage("staffJoin.vulnerability.high", player.getName());
            } else if (reportsCount >= reportsTillMed) {
                vulnerability = configManager.getMessage("staffJoin.vulnerability.medium", player.getName());
            } else {
                vulnerability = configManager.getMessage("staffJoin.vulnerability.low", player.getName());
            }

            Utils.sendText(connection, "staffJoin.message", message ->
                    message.replace("%reportsCount%", String.valueOf(reportsCount))
                            .replace("%vulnerability%", vulnerability)
                            .replace("%notify%", notify));

        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        storageManager.updatePlayerLastLogin(event.getPlayer().getUniqueId());
    }
}
