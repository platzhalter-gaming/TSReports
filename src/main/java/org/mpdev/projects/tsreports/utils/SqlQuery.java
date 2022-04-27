package org.mpdev.projects.tsreports.utils;

public enum SqlQuery {

    CREATE_REPORTS_TABLE(
            "CREATE TABLE IF NOT EXISTS `tsreports_reports` (" +
                    " `id` BIGINT(20) NOT NULL auto_increment," +
                    " `playerName` VARCHAR(16)," +
                    " `playerUuid` VARCHAR(72)," +
                    " `playerIp` VARCHAR(25)," +
                    " `targetName` VARCHAR(16)," +
                    " `targetUuid` VARCHAR(72)," +
                    " `targetIp` VARCHAR(25)," +
                    " `reason` VARCHAR(255)," +
                    " `flag` VARCHAR(72)," +
                    " `time` LONGTEXT," +
                    " PRIMARY KEY (`id`))"
    ),
    CREATE_REPORTHISTORY_TABLE(
            "CREATE TABLE IF NOT EXISTS `tsreports_reporthistory` (" +
                    " `id` BIGINT(20) NOT NULL auto_increment," +
                    " `playerName` VARCHAR(16)," +
                    " `playerUuid` VARCHAR(72)," +
                    " `playerIp` VARCHAR(25)," +
                    " `targetName` VARCHAR(16)," +
                    " `targetUuid` VARCHAR(72)," +
                    " `targetIp` VARCHAR(25)," +
                    " `reason` VARCHAR(255)," +
                    " `flag` VARCHAR(72)," +
                    " `time` LONGTEXT," +
                    " PRIMARY KEY (`id`))"
    ),
    CREATE_PLAYERS_TABLE(
            "CREATE TABLE IF NOT EXISTS `tsreports_players` (" +
                    " `uuid` VARCHAR(72) NOT NULL," +
                    " `name` VARCHAR(16)," +
                    " `ip` VARCHAR(25)," +
                    " `language` VARCHAR(10)," +
                    " `first_login` LONGTEXT NOT NULL," +
                    " `last_login` LONGTEXT NOT NULL," +
                    " `notify` VARCHAR(8)," +
                    " PRIMARY KEY (`uuid`))"
    ),
    ADD_COLUMN_IF_NOT_EXISTS(
            "ALTER TABLE {0} ADD COLUMN IF NOT EXISTS {1}"
    ),
    ADD_PLAYER_TO_PLAYERS_TABLE(
            "INSERT INTO `tsreports_players` (" +
                    " `uuid`, `name`, `ip`, `language`, `first_login`, `last_login`, `notify`)" +
                    " VALUES (?,?,?,?,?,?,?)"
    ),
    ADD_REPORT_TO_REPORTS(
            "INSERT INTO `tsreports_reports` (" +
                    "`playerName`, `playerUuid`, `playerIp`, `targetName`, `targetUuid`, `targetIp`, `reason`, `flag`, `time`)" +
                    " VALUES (?,?,?,?,?,?,?,?,?)"
    ),
    ADD_REPORT_TO_HISTORY(
            "INSERT INTO `tsreports_reports` (" +
                    "`playerName`, `playerUuid`, `playerIp`, `targetName`, `targetUuid`, `targetIp`, `reason`, `flag`, `time`)" +
                    " VALUES (?,?,?,?,?,?,?,?,?)"
    ),
    GET_REPORT("SELECT * FROM `tsreports_reports` WHERE id = ?"),
    GET_REPORTS_WITH_NAME("SELECT * FROM `tsreports_reports` WHERE playerName = ?"),
    GET_REPORTS_WITH_UUID("SELECT * FROM `tsreports_reports` WHERE playerUuid = ?"),
    GET_REPORTS_AS_LIST("SELECT * FROM `tsreports_reports` ORDER BY id DESC LIMIT ? OFFSET ?"),
    GET_REPORTS_AS_LIST_FROM_PLAYER_WITH_UUID("SELECT * FROM `tsreports_reports` WHERE playerUuid = ? ORDER BY id DESC LIMIT ? OFFSET ?"),
    GET_REPORTS_AS_LIST_FROM_PLAYER_WITH_NAME("SELECT * FROM `tsreports_reports` WHERE playerName = ? ORDER BY id DESC LIMIT ? OFFSET ?"),
    DELETE_REPORT("DELETE FROM `tsreports_reports` WHERE id = ?"),
    DELETE_ALL_REPORTS("DELETE FROM `tsreports_reports`"),
    DELETE_REPORTS_WITH_NAME("DELETE FROM `tsreports_reports` WHERE playerName = ?"),
    DELETE_REPORTS_WITH_UUID("DELETE FROM `tsreports_reports` WHERE playerUuid = ?"),
    SELECT_ALL_REPORTS("SELECT * FROM `tsreports_reports`"),
    SELECT_ALL_PLAYERS("SELECT * FROM `tsreports_players`"),
    GET_PLAYER_WITH_UUID("SELECT * FROM `tsreports_players` WHERE uuid = ?"),
    GET_PLAYER_WITH_NAME("SELECT * FROM `tsreports_players` WHERE name = ?"),
    UPDATE_PLAYER_LOCALE("UPDATE `tsreports_players` SET `language` = ? WHERE `uuid` = ?"),
    UPDATE_PLAYER_NAME("UPDATE `tsreports_players` SET `name` = ? WHERE `uuid` = ?"),
    UPDATE_PLAYER_LAST_LOGIN("UPDATE `tsreports_players` SET `last_login` = ? WHERE `uuid` = ?"),
    UPDATE_PLAYER_IP("UPDATE `tsreports_players` SET `ip` = ? WHERE `uuid` = ?");

    private final String query;
    SqlQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

}
