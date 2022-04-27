package org.mpdev.projects.tsreports.inventory.inventories;

import dev.simplix.protocolize.api.ClickType;
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

public class ManageReports extends UIFrame {

    private final Paginator paginator;
    private final StorageManager storageManager = TSReports.getInstance().getStorageManager();
    private final List<Report> reports = storageManager.getAllReports();

    public ManageReports(UIFrame parent, ProxiedPlayer viewer) {
        super(parent, viewer);
        paginator = new Paginator(getSize() - 9, reports);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_MANAGEREPORTS_TITLE.getString(getViewer().getName())
                .replace("%size%", "" + reports.size());
    }

    @Override
    public int getSize() {
        return 9 * 6;
    }

    @Override
    public void createComponents() {
        add(Components.getBackComponent(getParent(), 53, getViewer()));

        int slot = 0;
        for (int i = paginator.getMinIndex(); paginator.isValidIndex(i); i++) {
            Report report = reports.get(i);
            addReport(slot, report);
            slot++;
        }
        add(Components.getPreviousPageComponent(51, this::previousPage, paginator, getViewer()));
        add(Components.getNextPageComponent(52, this::nextPage, paginator, getViewer()));
    }

    private void addReport(int slot, Report report) {
        List<String> lore = Messages.GUI_MANAGEREPORTS_REPORT_LORE.getStringList(getViewer().getName())
                .stream().map(string -> Utils.replaceReportPlaceholders(string, report))
                .collect(Collectors.toList());
        UIComponent component = new UIComponentImpl.Builder(ItemType.PAPER)
                .name(Messages.GUI_MANAGEREPORTS_REPORT_NAME.getString(getViewer().getName())
                        .replace("%id%", "" + report.getId()))
                .lore(lore)
                .slot(slot)
                .build();
        component.setListener(ClickType.LEFT_CLICK, () -> {
            reports.remove(report);
            storageManager.deleteReport(report.getId());
        });
        component.setConfirmationRequired(ClickType.LEFT_CLICK);
        add(component);
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

}
