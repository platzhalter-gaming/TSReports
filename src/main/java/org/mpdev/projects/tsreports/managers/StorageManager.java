package org.mpdev.projects.tsreports.managers;

import net.md_5.bungee.config.Configuration;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.objects.Status;
import org.mpdev.projects.tsreports.storage.DBCore;
import org.mpdev.projects.tsreports.storage.H2Core;
import org.mpdev.projects.tsreports.storage.MySQLCore;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StorageManager {

    private final TSReports plugin = TSReports.getInstance();
    private DBCore core;

    public StorageManager() {
        initializeTables();
        checkNewColumns();
    }

    public void initializeTables() {
        Configuration config = plugin.getConfigManager().getConfig();
        if (config.getBoolean("mysql.enabled")) {
            core = new MySQLCore(
                    config.getString("mysql.host"),
                    config.getString("mysql.database"),
                    config.getInt("mysql.port"),
                    config.getString("mysql.username"),
                    config.getString("mysql.password"));
        } else {
            core = new H2Core();
        }
        if (!core.existsTable("tsreports_reports")) {
            plugin.getLogger().info("Creating table: tsreports_reports");
            String query = "CREATE TABLE IF NOT EXISTS tsreports_reports (" +
                    " id BIGINT(20) NOT NULL," +
                    " name VARCHAR(16)," +
                    " uuid VARCHAR(72)," +
                    " ip VARCHAR(25)," +
                    " reason VARCHAR(255)," +
                    " operator VARCHAR(16)," +
                    " status VARCHAR(20)," +
                    " claimed VARCHAR(72)," +
                    " PRIMARY KEY (id))";
            core.execute(query);
        }
        if (!core.existsTable("tsreports_reporthistory")) {
            plugin.getLogger().info("Creating table: tsreports_reporthistory");
            String query = "CREATE TABLE IF NOT EXISTS tsreports_reporthistory (" +
                    " id BIGINT(20) NOT NULL auto_increment," +
                    " name VARCHAR(16)," +
                    " uuid VARCHAR(72)," +
                    " ip VARCHAR(25)," +
                    " reason VARCHAR(255)," +
                    " operator VARCHAR(16)," +
                    " status VARCHAR(20)," +
                    " claimed VARCHAR(72)," +
                    " PRIMARY KEY (id))";
            core.execute(query);
        }
        if (!core.existsTable("tsreports_players")) {
            plugin.getLogger().info("Creating table: tsreports_players");
            String query = "CREATE TABLE IF NOT EXISTS tsreports_players (" +
                    " uuid VARCHAR(72) NOT NULL," +
                    " name VARCHAR(16)," +
                    " ip VARCHAR(25)," +
                    " language VARCHAR(10)," +
                    " loggedIn VARCHAR(10)," +
                    " PRIMARY KEY (uuid))";
            core.execute(query);
        }
    }

    public void checkNewColumns() {
        core.execute(String.format("ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s",
                "tsreports_reports", "server LONGTEXT DEFAULT 'ALL' NOT NULL AFTER operator"));
        core.execute(String.format("ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s",
                "tsreports_reporthistory", "server LONGTEXT DEFAULT 'ALL' NOT NULL AFTER operator"));
    }

    public String getStorageProvider() {
        return plugin.getConfigManager().getConfig().getBoolean("mysql.enabled") ? "MySQL" : "H2";
    }

    public void addReportToReports(Report report) {
        String query = String.format("INSERT INTO tsreports_reports (id, name, uuid, ip, reason, operator, status) VALUES ('%s','%s','%s','%s','%s','%s','%s')",
                report.getReportId(),
                report.getPlayerName(),
                report.getUniqueId().toString(),
                report.getAddress(),
                report.getReason(),
                report.getOperator(),
                report.getStatus().getStatusName());
        core.executeUpdateAsync(query);
    }

    public void addReportToHistory(Report report) {
        String query = String.format("INSERT INTO tsreports_reporthistory (name, uuid, ip, reason, operator, status) VALUES ('%s','%s','%s','%s','%s','%s')",
                report.getPlayerName(),
                report.getUniqueId().toString(),
                report.getAddress(),
                report.getReason(),
                report.getOperator(),
                report.getStatus().getStatusName());
        core.executeUpdateAsync(query);
    }

    public void deleteReport(int reportId) {
        String query = String.format("DELETE FROM tsreports_reports WHERE id = '%s'",
                reportId);
        core.executeUpdateAsync(query);
    }

    public Report getReport(int reportId) {
        String query = String.format("SELECT * FROM tsreports_reports WHERE id = '%s'", reportId);
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            if (result.next()) {
                String name = result.getString("name");
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String ip = result.getString("ip");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                String server = result.getString("server");
                Status status = getStatus(result.getString("status"));
                UUID claimed = null;
                if (result.getString("claimed") != null) {
                    claimed = UUID.fromString(result.getString("claimed"));
                }
                return new Report(name, uuid, ip, reason, operator, server, status, reportId, claimed);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OfflinePlayer getOfflinePlayer(UUID wantedPlayer) {
        String query = String.format("SELECT * FROM tsreports_players WHERE uuid = '%s'", wantedPlayer);
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            if (result.next()) {
                String name = result.getString("name");
                String ip = result.getString("ip");
                String[] l = result.getString("language").split("_");
                Locale locale = new Locale(l[0], l[1]);
                boolean loggedIn = result.getString("loggedIn").equals("true");
                return new OfflinePlayer(wantedPlayer, name, ip, locale, loggedIn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, OfflinePlayer> getAllOfflinePlayers() {
        String query = "SELECT * FROM tsreports_players";
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            Map<String, OfflinePlayer> offlinePlayers = new HashMap<>();
            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String name = result.getString("name");
                String ip = result.getString("ip");
                String[] l = result.getString("language").split("_");
                Locale locale = new Locale(l[0], l[1]);
                boolean loggedIn = result.getString("loggedIn").equals("true");
                offlinePlayers.put(name, new OfflinePlayer(uuid, name, ip, locale, loggedIn));
            }
            plugin.getLogger().info(offlinePlayers.size() + " offline players loaded.");
            return offlinePlayers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public int getReportsCount() {
        String query = "SELECT COUNT(*) FROM tsreports_reports";
        int count = 0;
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            result.next();
            count = result.getInt(1);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public int getStoredReportsCount() {
        String query = "SELECT COUNT(*) FROM tsreports_reporthistory";
        int count = 0;
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            result.next();
            count = result.getInt(1);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void addPlayer(OfflinePlayer player) {
        String query = String.format("INSERT INTO tsreports_players (uuid, name, ip, language, loggedIn) VALUES ('%s','%s','%s','%s','%s')",
                player.getUniqueId().toString(),
                player.getName(),
                player.getAddress(),
                player.getLocale().toString(),
                player.isLoggedIn() ? "true" : "false");
        core.executeUpdateAsync(query);
        plugin.debug(String.format("%s has been successfully added to the database.", player.getName()));
    }

    public void updatePlayerName(OfflinePlayer player) {
        String query = String.format("UPDATE tsreports_players SET name = '%s' WHERE uuid = '%s'",
                player.getName(),
                player.getUniqueId().toString());
        String oldName = TSReports.getInstance().getOfflinePlayers().get(player.getName()).getName();
        core.executeUpdateAsync(query);
        plugin.debug(String.format("Update player name: %s -> %s", oldName, player.getName()));
    }

    public void updatePlayerIp(OfflinePlayer player) {
        String query = String.format("UPDATE tsreports_players SET ip = '%s' WHERE uuid = '%s'",
                player.getAddress(),
                player.getUniqueId().toString());
        core.executeUpdateAsync(query);
    }

    public void updateLanguage(UUID uuid, Locale locale) {
        String query = String.format("UPDATE tsreports_players SET language = '%s' WHERE uuid = '%s'",
                locale.toString(),
                uuid.toString());
        core.executeUpdateAsync(query);
    }

    public void updateLoggedStatus(UUID uuid, boolean loggedIn) {
        String query = String.format("UPDATE tsreports_players SET loggedIn = '%s' WHERE uuid = '%s'",
                loggedIn ? "true" : "false",
                uuid.toString());
        core.executeUpdateAsync(query);
    }

    public void updateClaimed(int reportId, UUID claimed) {
        String query = String.format("UPDATE tsreports_reports SET claimed = '%s' WHERE id = '%s'",
                claimed.toString(),
                reportId);
        core.executeUpdateAsync(query);
    }

    public void updateStatus(int id, Status status) {
        String query = String.format("UPDATE tsreports_reports SET status = '%s' WHERE id = '%s'",
                status.getStatusName(),
                id);
        core.executeUpdateAsync(query);
    }

    public List<Report> getAllReports() {
        String query = "SELECT * FROM tsreports_reports";
        List<Report> reports = new ArrayList<>();
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            while (result.next()) {
                String name = result.getString("name");
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String ip = result.getString("ip");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                String server = result.getString("server");
                Status status = getStatus(result.getString("status"));
                int reportId = result.getInt("id");
                UUID claimed = null;
                if (result.getString("claimed") != null) {
                    claimed = UUID.fromString(result.getString("claimed"));
                }
                reports.add(new Report(name, uuid, ip, reason, operator, server, status, reportId, claimed));
            }
            return reports;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public void deleteAllReports() {
        String query = "DELETE FROM tsreports_reports";
        core.executeUpdateAsync(query);
    }

    public List<Report> getReportsAsList(int page, int perPage) {
        String query = String.format("SELECT * FROM tsreports_reports ORDER BY id DESC LIMIT %s OFFSET %s", perPage, (page - 1) * perPage);
        List<Report> reports = new ArrayList<>();
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            while (result.next()) {
                String name = result.getString("name");
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String ip = result.getString("ip");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                String server = result.getString("server");
                Status status = getStatus(result.getString("status"));
                int reportId = result.getInt("id");
                UUID claimed = null;
                if (result.getString("claimed") != null) {
                    claimed = UUID.fromString(result.getString("claimed"));
                }
                reports.add(new Report(name, uuid, ip, reason, operator, server, status, reportId, claimed));
            }
            return reports;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public List<Report> getReportsAsListFromPlayer(String operator, int page, int perPage) {
        String query = String.format("SELECT * FROM tsreports_reports WHERE operator = '%s' ORDER BY id DESC LIMIT %s OFFSET %s", operator, perPage, (page - 1) * perPage);
        List<Report> reports = new ArrayList<>();
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            while (result.next()) {
                String name = result.getString("name");
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String ip = result.getString("ip");
                String reason = result.getString("reason");
                String server = result.getString("server");
                Status status = getStatus(result.getString("status"));
                int reportId = result.getInt("id");
                UUID claimed = null;
                if (result.getString("claimed") != null) {
                    claimed = UUID.fromString(result.getString("claimed"));
                }
                reports.add(new Report(name, uuid, ip, reason, operator, server, status, reportId, claimed));
            }
            return reports;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public Status getStatus(String statusName) {
        Status status;
        switch (statusName) {
            default:
            case ("New"):
                status = Status.NEW;
                break;
            case ("WIP"):
                status = Status.WIP;
                break;
            case ("Complete"):
                status = Status.COMPLETE;
                break;
        }
        return status;
    }

}
