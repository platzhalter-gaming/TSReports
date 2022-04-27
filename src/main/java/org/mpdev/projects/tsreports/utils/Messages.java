package org.mpdev.projects.tsreports.utils;

import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.managers.ConfigManager;

import java.util.List;

public enum Messages {
    GUI_BACKBUTTON_NAME("gui.backbutton.name"),
    GUI_BARRIER_NAME("gui.barrier.name"),
    GUI_MAIN_TITLE("gui.main.title"),
    GUI_MAIN_LANGUAGESELECTOR_NAME("gui.main.languageselector.name"),
    GUI_MAIN_ADMINPANEL_NAME("gui.main.adminpanel.name"),
    GUI_MAIN_MANAGEREPORTS_NAME("gui.main.managereports.name"),
    GUI_ADMINPANEL_TITLE("gui.adminpanel.title"),
    GUI_ADMINPANEL_RELOAD_NAME("gui.adminpanel.reload.name"),
    GUI_ADMINPANEL_DEFAULTLANGUAGESELECTOR_NAME("gui.adminpanel.defaultlanguageselector.name"),
    GUI_MANAGEREPORTS_TITLE("gui.managereports.title"),
    GUI_MANAGEREPORTS_REPORT_NAME("gui.managereports.report.name"),
    GUI_MANAGEREPORTS_REPORT_LORE("gui.managereports.report.lore"),
    GUI_MANAGEREPORTS_NEXT_NAME("gui.managereports.next.name"),
    GUI_MANAGEREPORTS_PREVIOUS_NAME("gui.managereports.previous.name"),
    GUI_LANGUAGESELECTOR_TITLE("gui.languageselector.title"),
    GUI_CONFIRMATION_TITLE("gui.confirmation.title"),
    GUI_CONFIRMATION_CONFIRM_NAME("gui.confirmation.confirm.name"),
    GUI_CONFIRMATION_RETURN_NAME("gui.confirmation.return.name"),
    GUI_REPORTING_TITLE("gui.reporting.title"),
    GUI_REPORTING_REASON_HACKING("gui.reporting.reason.hacking"),
    GUI_REPORTING_REASON_FLYING("gui.reporting.reason.flying"),
    GUI_REPORTING_REASON_XRAY("gui.reporting.reason.xray"),
    GUI_REPORTING_REASON_GRIEFING("gui.reporting.reason.griefing"),
    GUI_REPORTING_REASON_NAME("gui.reporting.reason.name"),
    GUI_REPORTING_REASON_SKIN("gui.reporting.reason.skin"),
    GUI_REPORTING_REASON_OTHER("gui.reporting.reason.other");

    private final String path;
    Messages(String path) {
        this.path = path;
    }

    public String getString(String player) {
        ConfigManager configManager = TSReports.getInstance().getConfigManager();
        return configManager.getMessage(path, player);
    }

    public String getString() {
        ConfigManager configManager = TSReports.getInstance().getConfigManager();
        return configManager.getMessage(path, true);
    }

    public List<String> getStringList(String player) {
        ConfigManager configManager = TSReports.getInstance().getConfigManager();
        return configManager.getStringList(path, player);
    }

    public String getPath(){
        return path;
    }
}
