package org.mpdev.projects.tsreports.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.managers.StorageManager;
import org.mpdev.projects.tsreports.objects.Report;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

public class Utils {

    public static boolean isInteger(String value){
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public static String replaceReportPlaceholders(String message, Report report) {
        return message.replace("%playerName%", report.getPlayerName())
                .replace("%playerUuid%", report.getPlayerUuid().toString())
                .replace("%playerIp%", report.getPlayerIp())
                .replace("%targetName%", report.getTargetName())
                .replace("%targetUuid%", report.getTargetUuid().toString())
                .replace("%targetIp%", report.getTargetIp())
                .replace("%reason%", report.getReason())
                .replace("%time%", String.valueOf(report.getTime()))
                .replace("%id%", String.valueOf(report.getId()))
                .replace("%flag%", report.getFlag());
    }

    public static boolean isOnline(String uuidOrName) {
        return uuidOrName.length() == 36
                ? TSReports.getInstance().getProxy().getPlayer(UUID.fromString(uuidOrName)).isConnected()
                : TSReports.getInstance().getProxy().getPlayer(uuidOrName).isConnected();
    }

    public static boolean isOffline(String name) {
        return TSReports.getInstance().getOfflinePlayers().containsKey(name);
    }

    public static void setupMetrics() {
        Metrics metrics = new Metrics(TSReports.getInstance(), 14698);
        metrics.addCustomChart(new SimplePie("most_used_databases", () -> TSReports.getInstance().getStorageManager().getStorageProvider()));
        metrics.addCustomChart(new SingleLineChart("total_reports", () -> TSReports.getInstance().getStorageManager().getReportsCount()));
    }

    public static Locale stringToLocale(String loc) {
        String[] localeStr = loc.split("_");
        return new Locale(localeStr[0], localeStr[1]);
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static boolean isPluginEnabled(String pluginName){
        Plugin plugin = TSReports.getInstance().getProxy().getPluginManager().getPlugin(pluginName);
        return plugin != null;
    }

    public static void sendText(CommandSender sender, String path) {
        String message = TSReports.getInstance().getConfigManager().getMessage(path, sender.getName());

        sender.sendMessage(new TextComponent(color(message)));
    }

    public static void sendText(CommandSender sender, String path, Function<String, String> placeholders) {
        String message = TSReports.getInstance().getConfigManager().getMessage(path, sender.getName());

        message = placeholders.apply(message);

        sender.sendMessage(new TextComponent(color(message)));
    }

    public static String getPlayerIp(String uuidOrName) {
        ProxiedPlayer player = uuidOrName.length() == 36
                ? TSReports.getInstance().getProxy().getPlayer(UUID.fromString(uuidOrName))
                : TSReports.getInstance().getProxy().getPlayer(uuidOrName);
        StorageManager storageManager = TSReports.getInstance().getStorageManager();
        return player != null && player.isConnected() ? player.getSocketAddress().toString().substring(1).split(":")[0] : storageManager.getPlayer(uuidOrName).getPlayerIp();
    }

}
