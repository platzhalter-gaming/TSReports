package org.mpdev.projects.tsreports.objects;

import java.sql.Timestamp;
import java.util.UUID;

public class Report {

    private final int id;
    private final String playerName, playerIp, targetName, targetIp, reason;
    private String flag;
    private final UUID playerUuid, targetUuid;
    private final long time;

    public Report(String playerName, UUID playerUuid, String playerIp, String targetName, UUID targetUuid, String targetIp, String reason, String flag, int id) {
        this(playerName, playerUuid, playerIp, targetName, targetUuid, targetIp, reason, flag, new Timestamp(System.currentTimeMillis()).getTime(), id);
    }

    public Report(String playerName, UUID playerUuid, String playerIp, String targetName, UUID targetUuid, String targetIp, String reason, String flag, long time, int id) {
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.playerIp = playerIp;
        this.targetName = targetName;
        this.targetUuid = targetUuid;
        this.targetIp = targetIp;
        this.reason = reason;
        this.flag = flag;
        this.time = time;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public String getFlag() {
        return flag;
    }

    public String getPlayerIp() {
        return playerIp;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getReason() {
        return reason;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public String getTargetName() {
        return targetName;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public UUID getTargetUuid() {
        return targetUuid;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}