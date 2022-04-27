package org.mpdev.projects.tsreports.objects;

import org.mpdev.projects.tsreports.TSReports;

import java.util.Locale;
import java.util.UUID;

public class OfflinePlayer {

    private final Locale defaultLocale = TSReports.getInstance().getConfigManager().getDefaultLocale();
    private String name, playerIp;
    private UUID uuid;
    private Locale locale;
    private boolean notify;

    public OfflinePlayer(UUID uuid, String name, String playerIp, Locale locale) {
        this(uuid, name, playerIp, locale, false);
    }

    public OfflinePlayer(UUID uuid, String name, String playerIp, Locale locale, boolean notify) {
        this.uuid = uuid;
        this.name = name;
        this.playerIp = playerIp;
        this.locale = locale;
        this.notify = notify;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setUniqueId(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPlayerIp() {
        return playerIp;
    }

    public void setPlayerIp(String playerIp) {
        this.playerIp = playerIp;
    }

    public Locale getLocale() {
        return locale != null ? locale : defaultLocale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
