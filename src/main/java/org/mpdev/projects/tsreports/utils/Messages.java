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
    GUI_ADMINPANEL_CLEAR_DATABASE_NAME("gui.adminpanel.cleardatabase.name"),
    GUI_ADMINPANEL_STATUS_LORE("gui.adminpanel.status.lore"),
    GUI_ADMINPANEL_STATUS_NAME("gui.adminpanel.status.name"),
    GUI_MANAGEREPORTS_TITLE("gui.managereports.title"),
    GUI_MANAGEREPORTS_REPORT_NAME("gui.managereports.report.name"),
    GUI_MANAGEREPORTS_REPORT_LORE("gui.managereports.report.lore"),
    GUI_MANAGEREPORTS_NEXT_NAME("gui.managereports.next.name"),
    GUI_MANAGEREPORTS_PREVIOUS_NAME("gui.managereports.previous.name"),
    GUI_LANGUAGESELECTOR_TITLE("gui.languageselector.title"),
    GUI_CONFIRMATION_TITLE("gui.confirmation.title"),
    GUI_CONFIRMATION_CONFIRM_NAME("gui.confirmation.confirm.name"),
    GUI_CONFIRMATION_RETURN_NAME("gui.confirmation.return.name"),
    GUI_REPORTPANEL_TITLE("gui.reportpanel.title"),
    GUI_SPECIFICREPORT_TITLE("gui.specificreport.title"),
    GUI_SPECIFICREPORT_DELETE_NAME("gui.specificreport.delete.name"),
    GUI_SPECIFICREPORT_DELETE_LORE("gui.specificreport.delete.lore"),
    GUI_SPECIFICREPORT_INFO_NAME("gui.specificreport.info.name"),
    GUI_SPECIFICREPORT_INFO_LORE("gui.specificreport.info.lore"),
    GUI_SPECIFICREPORT_CLAIM_NAME("gui.specificreport.claim.name"),
    GUI_SPECIFICREPORT_CLAIM_LORE("gui.specificreport.claim.lore"),
    GUI_SPECIFICREPORT_COMPLETE_LORE("gui.specificreport.complete.lore"),
    GUI_SPECIFICREPORT_COMPLETE_NAME("gui.specificreport.complete.name"),
    GUI_STATUSPANEL_TITLE("gui.statuspanel.title"),
    GUI_STATUSPANEL_REPORT_NAME("gui.statuspanel.report.name"),
    GUI_STATUSPANEL_REPORT_LORE("gui.statuspanel.report.lore"),
    GUI_STATUSPANEL_REPORTSEMPTY("gui.statuspanel.reportsEmpty");

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
        return configManager.getMessage(path);
    }

    public List<String> getStringList(String player) {
        ConfigManager configManager = TSReports.getInstance().getConfigManager();
        return configManager.getStringList(path, player);
    }

    public String getPath(){
        return path;
    }

}
