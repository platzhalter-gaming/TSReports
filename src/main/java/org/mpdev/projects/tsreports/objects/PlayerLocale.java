package org.mpdev.projects.tsreports.objects;

import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.managers.ConfigManager;

import java.util.Locale;

public class PlayerLocale {

    private final ConfigManager configManager = TSReports.getInstance().getConfigManager();
    private final String playerName;

    public PlayerLocale(String playerName) {
        this.playerName = playerName;
    }

    public Locale getLocale() {
        return !"CONSOLE".equals(playerName) ? TSReports.getInstance().getOfflinePlayers().get(playerName).getLocale() : configManager.getDefaultLocale();
    }
}
