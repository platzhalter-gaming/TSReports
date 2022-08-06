package org.mpdev.projects.tsreports;

import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.objects.Status;
import org.mpdev.projects.tsreports.utils.PluginHelp;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TSRAPI {

    private static final TSReports plugin = TSReports.getInstance();

    public static void addReport(Report report) {
        plugin.getStorageManager().addReportToReports(report);
        plugin.getStorageManager().addReportToHistory(report);
    }

    public static void deleteReport(int reportId) {
        plugin.getStorageManager().deleteReport(reportId);
    }

    public static Report getReport(int reportId) {
        return plugin.getStorageManager().getReport(reportId);
    }

    public static OfflinePlayer getOfflinePlayer(String uuidOrName) {
        return uuidOrName.length() == 36
                ? plugin.getStorageManager().getOfflinePlayer(UUID.fromString(uuidOrName))
                : plugin.getOfflinePlayers().get(uuidOrName);
    }

    public static int getReportsCount() {
        return plugin.getStorageManager().getReportsCount();
    }

    public static int getTotalDataCount() {
        return plugin.getStorageManager().getReportsCount()
                + plugin.getStorageManager().getStoredReportsCount()
                + plugin.getOfflinePlayers().size();
    }

    public static void addOfflinePlayer(OfflinePlayer player) {
        plugin.getStorageManager().addPlayer(player);
    }

    public static void updateOfflinePlayer(UpdateType type, Object... values) {
        switch (type) {
            case IP:
                plugin.getStorageManager().updatePlayerIp((OfflinePlayer) values[0]);
                break;
            case NAME:
                plugin.getStorageManager().updatePlayerName((OfflinePlayer) values[0]);
                break;
            case LOGGEDSTATUS:
                plugin.getStorageManager().updateLoggedStatus((UUID) values[0], (Boolean) values[1]);
                break;
            case LANGUAGE:
                plugin.getStorageManager().updateLanguage((UUID) values[0], (Locale) values[1]);
                break;
        }
    }


    public static void updateReport(UpdateType type, Object... values) {
        switch (type) {
            case STATUS:
                plugin.getStorageManager().updateStatus((Integer) values[0], (Status) values[1]);
                break;
            case CLAIMED:
                plugin.getStorageManager().updateClaimed((Integer) values[0], (UUID) values[1]);
                break;
        }
    }

    public static List<Report> getAllReports() {
        return plugin.getStorageManager().getAllReports();
    }

    public static void deleteAllReports() {
        plugin.getStorageManager().deleteAllReports();
    }

    public static List<Report> getReportsAsList(int page, int perPage) {
        return plugin.getStorageManager().getReportsAsList(page, perPage);
    }

    public static List<Report> getReportsAsListFromPlayer(String operator, int page, int perPage) {
        return plugin.getStorageManager().getReportsAsListFromPlayer(operator, page, perPage);
    }

    public static void reloadPlugin() {
        plugin.getConfigManager().setup();
        plugin.setCommands( PluginHelp.setupCommands() );
    }

    private enum UpdateType {
        NAME, IP, LANGUAGE, LOGGEDSTATUS, STATUS, CLAIMED
    }

}
