package org.mpdev.projects.tsreports.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.managers.ConfigManager;
import org.mpdev.projects.tsreports.managers.StorageManager;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

public class Utils {

    public static Locale stringToLocale(String message) {
        String[] loc = message.split("_");
        return new Locale(loc[0], loc[1]);
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void setupMetrics() {
        Metrics metrics = new Metrics(TSReports.getInstance(), 14698);
        metrics.addCustomChart(new SimplePie("most_used_databases", () -> TSReports.getInstance().getStorageManager().getStorageProvider()));
        metrics.addCustomChart(new SingleLineChart("total_reports", () -> TSReports.getInstance().getStorageManager().getReportsCount()));
    }

    public static boolean isPluginEnabled(String pluginName) {
        Plugin plugin = TSReports.getInstance().getProxy().getPluginManager().getPlugin(pluginName);
        return plugin != null;
    }

    public static void sendText(CommandSender sender, String path) {
        String message = TSReports.getInstance().getConfigManager().getMessage(path, sender.getName());
        sender.sendMessage(new TextComponent(color(message)));
    }

    public static void sendText(CommandSender sender, String path, Function<String, String> placeholders) {
        String message = TSReports.getInstance().getConfigManager().getMessage(path, sender.getName(), placeholders);
        sender.sendMessage(new TextComponent(color(message)));
    }

    public static String getText(String playerName, String path, Function<String, String> placeholders) {
        return TSReports.getInstance().getConfigManager().getMessage(path, playerName, placeholders);
    }

    public static String replaceReportPlaceholders(String message, Report report) {
        return message.replace("%id%", "" + report.getReportId())
                .replace("%player%", report.getPlayerName())
                .replace("%uuid%", report.getUniqueId().toString())
                .replace("%operator%", report.getOperator())
                .replace("%reason%", report.getReason())
                .replace("%address%", report.getAddress())
                .replace("%server%", report.getServer())
                .replace("%status%", report.getStatus().getStatusName());
    }

    public static String replaceEmbedPlaceholders(String message, CommandSender player, int reportId) {
        return message.replace("%operator%", player.getName())
                .replace("%id%", "" + reportId);
    }

    public static String replaceStatusPlaceholders(String message, ProxiedPlayer player) {
        StorageManager storageManager = TSReports.getInstance().getStorageManager();
        ConfigManager configManager = TSReports.getInstance().getConfigManager();

        int reportsTillMed = TSReports.getInstance().getConfig().getInt("reportsTillMed");
        int reportsTillHigh = TSReports.getInstance().getConfig().getInt("reportsTillHigh");

        int reportsCount = storageManager.getReportsCount();
        String vulnerability;

        if (reportsCount >= reportsTillHigh) {
            vulnerability = configManager.getMessage("staffJoin.vulnerability.high", player.getName());
        } else if (reportsCount >= reportsTillMed) {
            vulnerability = configManager.getMessage("staffJoin.vulnerability.medium", player.getName());
        } else {
            vulnerability = configManager.getMessage("staffJoin.vulnerability.low", player.getName());
        }

        int totalData = storageManager.getReportsCount() + TSReports.getInstance().getOfflinePlayers().size() + storageManager.getStoredReportsCount();
        int staffOnline = (int) TSReports.getInstance().getOfflinePlayers().values().stream().filter(OfflinePlayer::isLoggedIn).count();

        return message.replace("%vulnerability%", vulnerability)
                .replace("%reportsCount%", "" + reportsCount)
                .replace("%totalData%", "" + totalData)
                .replace("%storageProvider%", storageManager.getStorageProvider())
                .replace("%staffOnline%", "" + staffOnline);
    }

    public static LuckPerms getLuckPerms() {
        if (isPluginEnabled("LuckPerms")) {
            try {
                return LuckPermsProvider.get();
            } catch (IllegalStateException e) {
                TSReports.getInstance().getLogger().log(Level.SEVERE, "There has been a problem with LuckPerms: " + e.getMessage(), e);
            }
        }
        return null;
    }

    public static boolean isOnline(String uuidOrName) {
        TSReports plugin = TSReports.getInstance();
        ProxiedPlayer player = uuidOrName.length() == 36
                ? plugin.getProxy().getPlayer(UUID.fromString(uuidOrName))
                : plugin.getProxy().getPlayer(uuidOrName);
        return player != null && player.isConnected();
    }

    public static boolean isOffline(String name) {
        TSReports plugin = TSReports.getInstance();
        return plugin.getOfflinePlayers().containsKey(name);
    }

    public static String getPlayerIp(UUID uniqueId) {
        ProxiedPlayer player = TSReports.getInstance().getProxy().getPlayer(uniqueId);
        StorageManager storageManager = TSReports.getInstance().getStorageManager();
        return player != null && player.isConnected() ? player.getSocketAddress().toString().substring(1).split(":")[0] : storageManager.getOfflinePlayer(uniqueId).getAddress();
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean hasPermission(CommandSender sender, List<String> permissions) {
        for (String permission : permissions) {
            if (isPluginEnabled("LuckPerms") && sender instanceof ProxiedPlayer) {
                if (Luck.getPermissionData((ProxiedPlayer) sender).checkPermission(permission).asBoolean()) return true;
                if ((permissions.size() - 1) == 0) return false;
                continue;
            }
            if (sender.hasPermission(permission)) return true;
            if ((permissions.size() - 1) == 0) return false;
        }
        return false;
    }

}
