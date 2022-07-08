package org.mpdev.projects.tsreports.inventory.inventories;

import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.inventory.*;
import org.mpdev.projects.tsreports.managers.StorageManager;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.utils.Messages;
import org.mpdev.projects.tsreports.utils.Paginator;
import org.mpdev.projects.tsreports.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class StatusPanel extends UIFrame {

    private final Paginator paginator;
    private final List<Report> reports;

    public StatusPanel(UIFrame parent, ProxiedPlayer viewer, int page) {
        super(parent, viewer);
        int listPageLimit = TSReports.getInstance().getConfig().getInt("listPageLimit", 10);
        StorageManager storageManager = TSReports.getInstance().getStorageManager();
        this.reports = storageManager.getReportsAsListFromPlayer(getViewer().getName(), page, listPageLimit);
        paginator = new Paginator(getSize() - 9, reports);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_STATUSPANEL_TITLE.getString(getViewer().getName());
    }

    @Override
    public int getSize() {
        return 9 * 6;
    }

    @Override
    public void createComponents() {
        add(Components.getBackComponent(getParent(), 53, getViewer()));

        if (reports.isEmpty()) {
            add(new UIComponentImpl.Builder(ItemType.RED_TULIP)
                    .name(Messages.GUI_STATUSPANEL_REPORTSEMPTY.getString(getViewer().getName()))
                    .slot(13)
                    .build());
            add(new UIComponentImpl.Builder(ItemType.RED_TULIP)
                    .name(Messages.GUI_STATUSPANEL_REPORTSEMPTY.getString(getViewer().getName()))
                    .slot(40)
                    .build());
            return;
        }

        int slot = 0;
        for (int i = paginator.getMinIndex(); paginator.isValidIndex(i); i++) {
            Report report = reports.get(i);
            addReport(slot, report);
            slot++;
        }
        add(Components.getPreviousPageComponent(51, this::previousPage, paginator, getViewer()));
        add(Components.getNextPageComponent(52, this::nextPage, paginator, getViewer()));
    }

    private void previousPage() {
        if (paginator.previousPage()) {
            updateFrame();
        }
    }

    private void nextPage() {
        if (paginator.nextPage()) {
            updateFrame();
        }
    }

    private void updateFrame() {
        InventoryDrawer.open(this);
    }

    private void addReport(int slot, Report report) {
        List<String> lore = Messages.GUI_STATUSPANEL_REPORT_LORE.getStringList(getViewer().getName())
                .stream().map(string -> Utils.replaceReportPlaceholders(string, report))
                .collect(Collectors.toList());
        UIComponent component = new UIComponentImpl.Builder(ItemType.PAPER)
                .name(Messages.GUI_STATUSPANEL_REPORT_NAME.getString(getViewer().getName())
                        .replace("%id%", "" + report.getReportId()))
                .lore(lore)
                .slot(slot)
                .build();
        add(component);
    }

}
