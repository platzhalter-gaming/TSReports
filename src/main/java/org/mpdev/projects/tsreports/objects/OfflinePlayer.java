package org.mpdev.projects.tsreports.objects;

import org.mpdev.projects.tsreports.TSReports;

import java.util.Locale;
import java.util.UUID;

public class OfflinePlayer {

    private final UUID uuid;
    private String name, address;
    private Locale locale;
    private boolean loggedIn;

    public OfflinePlayer(UUID uuid, String name, String address) {
        this(uuid, name, address, TSReports.getInstance().getConfigManager().getDefaultLocale(), false);
    }

    public OfflinePlayer(UUID uuid, String name, String address, Locale locale, boolean loggedIn) {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
        this.locale = locale;
        this.loggedIn = loggedIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public UUID getUniqueId() {
        return uuid;
    }

}
