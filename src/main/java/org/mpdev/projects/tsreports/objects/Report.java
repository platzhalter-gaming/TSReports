package org.mpdev.projects.tsreports.objects;

import java.util.UUID;

public class Report {

    private final int reportId;
    private String playerName, address, operator;
    private final String reason, server;
    private final UUID uuid;
    private UUID claimed;
    private Status status;

    public Report(String playerName, UUID uuid, String address, String reason, String operator, String server, Status status, int reportId) {
        this(playerName, uuid, address, reason, operator, server, status, reportId, null);
    }

    public Report(String playerName, UUID uuid, String address, String reason, String operator, String server, Status status, int reportId, UUID claimed) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.address = address;
        this.reason = reason;
        this.operator = operator;
        this.server = server;
        this.status = status;
        this.reportId = reportId;
        this.claimed = claimed;
    }

    public UUID getClaimed() {
        return claimed;
    }

    public void setClaimed(UUID claimed) {
        this.claimed = claimed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getServer() {
        return server;
    }

    public String getReason() {
        return reason;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getReportId() {
        return reportId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public UUID getUniqueId() {
        return uuid;
    }

}
