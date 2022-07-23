package org.mpdev.projects.tsreports.inventory.inventories;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.Sound;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.inventory.*;
import org.mpdev.projects.tsreports.managers.DiscordManager;
import org.mpdev.projects.tsreports.managers.StorageManager;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.objects.Status;
import org.mpdev.projects.tsreports.utils.Messages;
import org.mpdev.projects.tsreports.utils.Paginator;
import org.mpdev.projects.tsreports.utils.Utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ManageReports extends UIFrame {

    private final Paginator paginator;
    private final List<Report> reports;

    public ManageReports(UIFrame parent, ProxiedPlayer viewer) {
        super(parent, viewer);
        this.reports = TSReports.getInstance().getStorageManager().getAllReports();
        paginator = new Paginator(getSize() - 9, reports);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_MANAGEREPORTS_TITLE.getString(getViewer().getName())
                .replace("{0}", "" + reports.size());
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
        UIComponent previous = Components.getPreviousPageComponent(51, this::previousPage, paginator, getViewer());
        if (previous != null) add(previous);
        UIComponent next = Components.getNextPageComponent(52, this::nextPage, paginator, getViewer());
        if (next != null) add(next);
    }

    private void addReport(int slot, Report report) {
        List<String> lore = Messages.GUI_MANAGEREPORTS_REPORT_LORE.getStringList(getViewer().getName())
                .stream().map(string -> Utils.replaceReportPlaceholders(string, report))
                .collect(Collectors.toList());
        UIComponent component = new UIComponentImpl.Builder(ItemType.PAPER)
                .name(Messages.GUI_MANAGEREPORTS_REPORT_NAME.getString(getViewer().getName())
                        .replace("%id%", "" + report.getReportId()))
                .lore(lore)
                .slot(slot)
                .build();
        component.setListener(ClickType.LEFT_CLICK, () -> {
            if (report.getStatus().equals(Status.COMPLETE) && !Utils.hasPermission(getViewer(), Collections.singletonList("tsreports.admin"))) {
                Utils.sendText(getViewer(), "alreadyCompleted");
                return;
            }
            InventoryDrawer.open(new SpecificReport(this, getViewer(), report));
        });
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

    public static class SpecificReport extends UIFrame {

        private final Report report;
        private final StorageManager storageManager = TSReports.getInstance().getStorageManager();
        private final DiscordManager discordManager = TSReports.getInstance().getDiscordManager();

        public SpecificReport(UIFrame parent, ProxiedPlayer viewer, Report report) {
            super(parent, viewer);
            this.report = report;
        }

        @Override
        public String getTitle() {
            return Messages.GUI_SPECIFICREPORT_TITLE.getString(getViewer().getName())
                    .replace("%id%", "" + report.getReportId());
        }

        @Override
        public int getSize() {
            return 9 * 3;
        }

        @Override
        public void createComponents() {
            add(Components.getBackComponent(getParent(), 26, getViewer()));
            addDeleteButton();
            addClaimButton();
            addCompleteButton();
            addInfoButton();
        }

        public void addDeleteButton() {
            List<String> lore = Messages.GUI_SPECIFICREPORT_DELETE_LORE.getStringList(getViewer().getName());
            UIComponent component = new UIComponentImpl.Builder(ItemType.RED_STAINED_GLASS_PANE)
                    .lore(lore)
                    .name(Messages.GUI_SPECIFICREPORT_DELETE_NAME.getString(getViewer().getName()))
                    .slot(10)
                    .build();
            component.setPermission(ClickType.LEFT_CLICK, "tsreports.delete");
            component.setListener(ClickType.LEFT_CLICK, () -> {
                if (!Utils.hasPermission(getViewer(), Collections.singletonList("tsreports.admin"))) {
                    if (report.getClaimed() != null && !report.getClaimed().equals(getViewer().getUniqueId())) {
                        Utils.sendText(getViewer(), "cannotDeleteClaimedReport");
                        return;
                    }
                }
                storageManager.deleteReport(report.getReportId());
                Utils.sendText(getViewer(), "command-messages.delete", s -> s.replace("%id%", "" + report.getReportId()));
                discordManager.sendDeleteEmbed(getViewer(), report.getReportId());
                updateFrame();
            });
            component.setConfirmationRequired(ClickType.LEFT_CLICK);
            add(component);
        }

        public void addClaimButton() {
            List<String> lore = Messages.GUI_SPECIFICREPORT_CLAIM_LORE.getStringList(getViewer().getName());
            UIComponent component = new UIComponentImpl.Builder(ItemType.YELLOW_STAINED_GLASS_PANE)
                    .lore(lore)
                    .name(Messages.GUI_SPECIFICREPORT_CLAIM_NAME.getString(getViewer().getName()))
                    .slot(12)
                    .build();
            component.setPermission(ClickType.LEFT_CLICK, "tsreports.claim");
            component.setListener(ClickType.LEFT_CLICK, () -> {
                if (report.getClaimed() != null && (report.getClaimed().equals(getViewer().getUniqueId()) || !report.getClaimed().equals(getViewer().getUniqueId()))) {
                    Utils.sendText(getViewer(), "alreadyClaimed");
                    InventoryDrawer.playSound(getViewer(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS);
                    return;
                }
                storageManager.updateClaimed(report.getReportId(), getViewer().getUniqueId());
                report.setClaimed(getViewer().getUniqueId());
                storageManager.updateStatus(report.getReportId(), Status.WIP);
                report.setStatus(Status.WIP);
                InventoryDrawer.playSound(getViewer(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS);
                Utils.sendText(getViewer(), "command-messages.claim", s -> s.replace("%id%", "" + report.getReportId()));
                discordManager.sendClaimEmbed(getViewer(), report.getReportId());
                updateFrame();
            });
            add(component);
        }

        public void addCompleteButton() {
            List<String> lore = Messages.GUI_SPECIFICREPORT_COMPLETE_LORE.getStringList(getViewer().getName());
            UIComponent component = new UIComponentImpl.Builder(ItemType.BLUE_STAINED_GLASS_PANE)
                    .lore(lore)
                    .name(Messages.GUI_SPECIFICREPORT_COMPLETE_NAME.getString(getViewer().getName()))
                    .slot(14)
                    .build();
            component.setListener(ClickType.LEFT_CLICK, () -> {
                if (report.getClaimed() == null || !report.getClaimed().equals(getViewer().getUniqueId())) {
                    Utils.sendText(getViewer(), "notClaimed");
                    InventoryDrawer.playSound(getViewer(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS);
                    return;
                }
                storageManager.updateStatus(report.getReportId(), Status.COMPLETE);
                report.setStatus(Status.COMPLETE);
                InventoryDrawer.playSound(getViewer(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS);
                discordManager.sendCompleteEmbed(getViewer(), report.getReportId());
                updateFrame();
            });
            add(component);
        }

        public void addInfoButton() {
            List<String> lore = Messages.GUI_SPECIFICREPORT_INFO_LORE.getStringList(getViewer().getName())
                    .stream().map(string -> Utils.replaceReportPlaceholders(string, report))
                    .collect(Collectors.toList());
            UIComponent component = new UIComponentImpl.Builder(ItemType.GREEN_STAINED_GLASS_PANE)
                    .lore(lore)
                    .name(Messages.GUI_SPECIFICREPORT_INFO_NAME.getString(getViewer().getName()))
                    .slot(16)
                    .build();
            add(component);
        }

        public void updateFrame() {
            InventoryDrawer.open(new ManageReports(new MainFrame(getViewer()), getViewer()));
        }

    }

}
