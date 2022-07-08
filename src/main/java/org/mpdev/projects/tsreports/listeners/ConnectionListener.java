package org.mpdev.projects.tsreports.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
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

    public ConnectionListener(TSReports plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
        this.configManager = plugin.getConfigManager();
    }

    //TODO: plugin.getOfflinePlayers().replace(player.getUniqueId().toString(), new OfflinePlayer(player));
    @EventHandler
    public void onLogin(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        if (!connection.isConnected()) return;
        OfflinePlayer player = new OfflinePlayer(
                connection.getUniqueId(),
                connection.getName(),
                connection.getSocketAddress().toString().substring(1).split(":")[0]);

        // If the player is entering the server for the first time, save it
        if (!plugin.getOfflinePlayers().containsKey(player.getName())) {

            plugin.debug(String.format("%s is entering the server for the first time.", player.getName()));
            storageManager.addPlayer(player);
            plugin.getOfflinePlayers().put(player.getName(), player);

        } else {

            storageManager.updatePlayerName(player);
            storageManager.updatePlayerIp(player);

        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer connection = event.getPlayer();
        OfflinePlayer player = plugin.getOfflinePlayers().get(event.getPlayer().getName());

        // Send server wealth message (only staff members)
        int reportsTillMed = plugin.getConfig().getInt("reportsTillMed");
        int reportsTillHigh = plugin.getConfig().getInt("reportsTillHigh");

        if (Utils.hasPermission(connection, "tsreports.staff") || Utils.hasPermission(connection, "tsreports.admin")) {

            int reportsCount = storageManager.getReportsCount();
            String vulnerability;
            String loggedIn = player.isLoggedIn() ? "&aLogged In&7" : "&cLogged Out&7";

            if (reportsCount >= reportsTillHigh) {
                vulnerability = configManager.getMessage("staffJoin.vulnerability.high", player.getName());
            } else if (reportsCount >= reportsTillMed) {
                vulnerability = configManager.getMessage("staffJoin.vulnerability.medium", player.getName());
            } else {
                vulnerability = configManager.getMessage("staffJoin.vulnerability.low", player.getName());
            }

            Utils.sendText(connection, "staffJoin.message", message -> message.replace("%reportsCount%", String.valueOf(reportsCount)).replace("%vulnerability%", vulnerability).replace("%loggedIn%", loggedIn));

        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        OfflinePlayer player = plugin.getOfflinePlayers().get(event.getPlayer().getName());
        if (!Utils.hasPermission(event.getPlayer(), "tsreports.autologin") && player.isLoggedIn()) {
            player.setLoggedIn(false);
        }
    }

}
