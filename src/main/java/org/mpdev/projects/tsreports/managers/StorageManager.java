package org.mpdev.projects.tsreports.managers;

import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.config.Configuration;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.utils.SqlQuery;

import java.sql.*;
import java.util.*;

public class StorageManager {

    private final TSReports plugin;
    private final HikariDataSource source = new HikariDataSource();
    boolean mysqlEnabled;

    public StorageManager(TSReports plugin) {
        this.plugin = plugin;
        Configuration config = plugin.getConfig();
        String pluginName = plugin.getDescription().getName();
        source.setPoolName("[" + pluginName + "]" + " Hikari");
        mysqlEnabled = plugin.getConfig().getBoolean("mysql.enabled");
        plugin.getLogger().info("Loading storage provider: " + getStorageProvider());
        if (mysqlEnabled) {
            source.setJdbcUrl("jdbc:mysql://" + config.getString("mysql.host") + ":" + config.getString("mysql.port") + "/" + config.getString("mysql.database") + "?useSSL=false&characterEncoding=utf-8");
            source.setUsername(config.getString("mysql.username"));
            source.setPassword(config.getString("mysql.password"));
        } else {
            source.setDriverClassName("org.h2.Driver");
            source.setJdbcUrl("jdbc:h2:./plugins/" + pluginName + "/" + pluginName);
        }
        setup();
    }

    public String getStorageProvider(){
        return mysqlEnabled ? "MySQL" : "H2";
    }

    private void executeUpdate(String query) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setup() {
        executeUpdate(SqlQuery.CREATE_REPORTS_TABLE.getQuery());
        executeUpdate(SqlQuery.CREATE_REPORTHISTORY_TABLE.getQuery());
        executeUpdate(SqlQuery.CREATE_PLAYERS_TABLE.getQuery());
        executeUpdate(SqlQuery.ADD_COLUMN_IF_NOT_EXISTS.getQuery()
                .replace("{0}", "tsreports_players")
                .replace("{1}", "first_login LONGTEXT NOT NULL"));
        executeUpdate(SqlQuery.ADD_COLUMN_IF_NOT_EXISTS.getQuery()
                .replace("{0}", "tsreports_players")
                .replace("{1}", "last_login LONGTEXT NOT NULL"));
    }

    public void addReportToReports(Report report) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.ADD_REPORT_TO_REPORTS.getQuery())) {
            ps.setString(1, report.getPlayerName());
            ps.setString(2, report.getPlayerUuid().toString());
            ps.setString(3, report.getPlayerIp());
            ps.setString(4, report.getTargetName());
            ps.setString(5, report.getTargetUuid().toString());
            ps.setString(6, report.getTargetIp());
            ps.setString(7, report.getReason());
            ps.setString(8, String.valueOf(report.getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addReportToHistory(Report report) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.ADD_REPORT_TO_HISTORY.getQuery())) {
            ps.setString(1, report.getPlayerName());
            ps.setString(2, report.getPlayerUuid().toString());
            ps.setString(3, report.getPlayerIp());
            ps.setString(4, report.getTargetName());
            ps.setString(5, report.getTargetUuid().toString());
            ps.setString(6, report.getTargetIp());
            ps.setString(7, report.getReason());
            ps.setString(8, String.valueOf(report.getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayer(OfflinePlayer player) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.ADD_PLAYER_TO_PLAYERS_TABLE.getQuery())) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.setString(3, player.getPlayerIp());
            ps.setString(4, plugin.getConfigManager().getDefaultLocale().toString());
            ps.setString(5, String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()));
            ps.setString(6, String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()));
            ps.setString(7, player.isNotify() ? "enabled" : "disabled");
            ps.executeUpdate();
            plugin.debug(String.format("%s has been added to the database.", player.getName()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetReports() {
        executeUpdate(SqlQuery.DELETE_ALL_REPORTS.getQuery());
        plugin.debug("All reports have been deleted!");
    }

    public Report getReport(int reportId) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.GET_REPORT.getQuery())) {
            ps.setInt(1, reportId);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                String playerName = result.getString("playerName");
                UUID playerUuid = UUID.fromString(result.getString("playerUuid"));
                String playerIp = result.getString("playerIp");
                String targetName = result.getString("targetName");
                UUID targetUuid = UUID.fromString(result.getString("targetUuid"));
                String targetIp = result.getString("targetIp");
                String reason = result.getString("reason");
                String flag = result.getString("flag");
                long time = result.getLong("time");
                int id = result.getInt("id");
                return new Report(playerName, playerUuid, playerIp, targetName, targetUuid, targetIp, reason, flag, time, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Report> getReportsFromPlayer(String uuidOrName) {
        String query = uuidOrName.length() == 36
                ? SqlQuery.GET_REPORTS_WITH_UUID.getQuery()
                : SqlQuery.GET_REPORTS_WITH_NAME.getQuery();
        List<Report> reports = new ArrayList<>();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuidOrName);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                String playerName = result.getString("playerName");
                UUID playerUuid = UUID.fromString(result.getString("playerUuid"));
                String playerIp = result.getString("playerIp");
                String targetName = result.getString("targetName");
                UUID targetUuid = UUID.fromString(result.getString("targetUuid"));
                String targetIp = result.getString("targetIp");
                String reason = result.getString("reason");
                String flag = result.getString("flag");
                long time = result.getLong("time");
                int id = result.getInt("id");
                reports.add(new Report(playerName, playerUuid, playerIp, targetName, targetUuid, targetIp, reason, flag, time, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public void deleteReport(int reportId) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.DELETE_REPORT.getQuery())) {
            ps.setInt(1, reportId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteReportsFromPlayer(String uuidOrName) {
        String query = uuidOrName.length() == 36
                ? SqlQuery.DELETE_REPORTS_WITH_UUID.getQuery()
                : SqlQuery.DELETE_REPORTS_WITH_NAME.getQuery();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuidOrName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.SELECT_ALL_REPORTS.getQuery())) {
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                String playerName = result.getString("playerName");
                UUID playerUuid = UUID.fromString(result.getString("playerUuid"));
                String playerIp = result.getString("playerIp");
                String targetName = result.getString("targetName");
                UUID targetUuid = UUID.fromString(result.getString("targetUuid"));
                String targetIp = result.getString("targetIp");
                String reason = result.getString("reason");
                String flag = result.getString("flag");
                long time = result.getLong("time");
                int id = result.getInt("id");
                reports.add(new Report(playerName, playerUuid, playerIp, targetName, targetUuid, targetIp, reason, flag, time, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public Map<String, OfflinePlayer> getAllPlayers() {
        Map<String, OfflinePlayer> offlinePlayers = new HashMap<>();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.SELECT_ALL_PLAYERS.getQuery())) {
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String name = result.getString("name");
                String ip = result.getString("ip");
                String[] l = result.getString("language").split("_");
                Locale language = new Locale(l[0], l[1]);
                boolean notify = result.getString("notify").equals("enabled");
                offlinePlayers.put(name, new OfflinePlayer(uuid, name, ip, language, notify));
            }
            plugin.getLogger().info(offlinePlayers.size() + " offline players loaded.");
            return offlinePlayers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offlinePlayers;
    }

    public OfflinePlayer getPlayer(String uuidOrName) {
        String query = uuidOrName.length() == 36
                ? SqlQuery.GET_PLAYER_WITH_UUID.getQuery()
                : SqlQuery.GET_PLAYER_WITH_NAME.getQuery();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuidOrName);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String name = result.getString("name");
                String ip = result.getString("ip");
                String[] l = result.getString("language").split("_");
                Locale language = new Locale(l[0], l[1]);
                boolean notify = result.getString("notify").equals("enabled");
                return new OfflinePlayer(uuid, name, ip, language, notify);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getReportsCount() {
        int count = 0;
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM `tsreports_reports`")) {
            ResultSet result = ps.executeQuery();
            result.next();
            count = result.getInt(1);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void updatePlayerName(OfflinePlayer player) {
        String oldName = TSReports.getInstance().getOfflinePlayers().get(player.getName()).getName();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.UPDATE_PLAYER_NAME.getQuery())) {
            ps.setString(1, player.getName());
            ps.setString(2, player.getUniqueId().toString());
            plugin.debug(String.format("Update player name: %s -> %s", oldName, player.getName()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerLastLogin(UUID uuid) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.UPDATE_PLAYER_LAST_LOGIN.getQuery())) {
            ps.setLong(1, new Timestamp(System.currentTimeMillis()).getTime());
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerIp(OfflinePlayer player) {
        String newIp = player.getPlayerIp();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.UPDATE_PLAYER_IP.getQuery())) {
            ps.setString(1, newIp);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLanguage(UUID uuid, Locale locale) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.UPDATE_PLAYER_LOCALE.getQuery())) {
            ps.setString(1, locale.toString());
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Report> getReportsAsList(int page, int perPage) {
        List<Report> reports = new ArrayList<>();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.GET_REPORTS_AS_LIST.getQuery())) {
            ps.setInt(1, perPage);
            ps.setInt(2, (page - 1) * perPage);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                String playerName = result.getString("playerName");
                UUID playerUuid = UUID.fromString(result.getString("playerUuid"));
                String playerIp = result.getString("playerIp");
                String targetName = result.getString("targetName");
                UUID targetUuid = UUID.fromString(result.getString("targetUuid"));
                String targetIp = result.getString("targetIp");
                String reason = result.getString("reason");
                String flag = result.getString("flag");
                long time = result.getLong("time");
                int id = result.getInt("id");
                reports.add(new Report(playerName, playerUuid, playerIp, targetName, targetUuid, targetIp, reason, flag, time, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public List<Report> getReportsAsListFromPlayer(String uuidOrName, int page, int perPage) {
        String query = uuidOrName.length() == 36
                ? SqlQuery.GET_REPORTS_AS_LIST_FROM_PLAYER_WITH_UUID.getQuery()
                : SqlQuery.GET_REPORTS_AS_LIST_FROM_PLAYER_WITH_NAME.getQuery();
        List<Report> reports = new ArrayList<>();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuidOrName);
            ps.setInt(2, perPage);
            ps.setInt(3, (page - 1) * perPage);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                String playerName = result.getString("playerName");
                UUID playerUuid = UUID.fromString(result.getString("playerUuid"));
                String playerIp = result.getString("playerIp");
                String targetName = result.getString("targetName");
                UUID targetUuid = UUID.fromString(result.getString("targetUuid"));
                String targetIp = result.getString("targetIp");
                String reason = result.getString("reason");
                String flag = result.getString("flag");
                long time = result.getLong("time");
                int id = result.getInt("id");
                reports.add(new Report(playerName, playerUuid, playerIp, targetName, targetUuid, targetIp, reason, flag, time, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

}