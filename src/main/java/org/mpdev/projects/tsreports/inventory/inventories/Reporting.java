package org.mpdev.projects.tsreports.inventory.inventories;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.events.ReportEvent;
import org.mpdev.projects.tsreports.inventory.Components;
import org.mpdev.projects.tsreports.inventory.UIComponent;
import org.mpdev.projects.tsreports.inventory.UIComponentImpl;
import org.mpdev.projects.tsreports.inventory.UIFrame;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.utils.Messages;
import org.mpdev.projects.tsreports.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Reporting extends UIFrame {

    public ProxiedPlayer ppTarget;
    public OfflinePlayer opTarget;
    public List<UIComponent> components = new ArrayList<>();

    public Reporting(ProxiedPlayer viewer, ProxiedPlayer target) {
        super(null, viewer);
        this.ppTarget = target;
    }

    public Reporting(ProxiedPlayer viewer, OfflinePlayer target) {
        super(null, viewer);
        this.opTarget = target;
    }

    @Override
    public String getTitle() {
        return Messages.GUI_REPORTING_TITLE.getString(getViewer().getName());
    }

    @Override
    public int getSize() {
        return 9 * 4;
    }

    @Override
    public void createComponents() {
        for (int i = 0; i < 8; i++) {
            add(Components.getBarrierComponent(i, getViewer()));
        }
        add(Components.getBarrierComponent(8, getViewer()));
        for (int i = 27; i < 35; i++) {
            add(Components.getBarrierComponent(i, getViewer()));
        }
        add(Components.getBarrierComponent(35, getViewer()));

        components.add(new UIComponentImpl.Builder(ItemType.IRON_SWORD)
                .name(Messages.GUI_REPORTING_REASON_HACKING.getString(getViewer().getName()))
                .slot(10)
                .build());
        components.add(new UIComponentImpl.Builder(ItemType.FEATHER)
                .name(Messages.GUI_REPORTING_REASON_FLYING.getString(getViewer().getName()))
                .slot(12)
                .build());
        components.add(new UIComponentImpl.Builder(ItemType.DIAMOND_ORE)
                .name(Messages.GUI_REPORTING_REASON_XRAY.getString(getViewer().getName()))
                .slot(14)
                .build());
        components.add(new UIComponentImpl.Builder(ItemType.TNT)
                .name(Messages.GUI_REPORTING_REASON_GRIEFING.getString(getViewer().getName()))
                .slot(16)
                .build());
        components.add(new UIComponentImpl.Builder(ItemType.NAME_TAG)
                .name(Messages.GUI_REPORTING_REASON_NAME.getString(getViewer().getName()))
                .slot(20)
                .build());
        components.add(new UIComponentImpl.Builder(ItemType.SKELETON_SKULL)
                .name(Messages.GUI_REPORTING_REASON_NAME.getString(getViewer().getName()))
                .slot(22)
                .build());
        components.add(new UIComponentImpl.Builder(ItemType.PAPER)
                .name(Messages.GUI_REPORTING_REASON_OTHER.getString(getViewer().getName()))
                .slot(24)
                .build());
        addReasons();
    }

    public void addReasons() {
        for (UIComponent component : components) {
            String name = component.getItem().displayName().toString().toLowerCase(Locale.ROOT);
            if (name.contains("hacking")) {
                setListener(component, "Hacking");
            } else if (name.contains("flying")) {
                setListener(component, "Flying");
            } else if (name.contains("xray") || name.contains("x-ray")) {
                setListener(component, "X-Ray");
            } else if (name.contains("griefing")) {
                setListener(component, "Griefing");
            } else if (name.contains("name")) {
                setListener(component, "Name");
            } else if (name.contains("skin")) {
                setListener(component, "Skin");
            } else {
                setListener(component, "Other");
            }
        }
    }

    public void setListener(UIComponent component, String reason) {
        component.setListener(ClickType.LEFT_CLICK, () -> {
            if (ppTarget == null) {
                callReportEvent(getViewer(), opTarget, reason);
            } else {
                callReportEvent(getViewer(), ppTarget, reason);
            }
        });
        add(component);
    }

    public static void callReportEvent(ProxiedPlayer player, ProxiedPlayer target, String reason) {
        TSReports.getInstance().getProxy().getPluginManager().callEvent(new ReportEvent(new Report(player.getName(), player.getUniqueId(), Utils.getPlayerIp(player.getName()), target.getName(), target.getUniqueId(), Utils.getPlayerIp(target.getName()), reason, "open", TSReports.getInstance().getStorageManager().getReportsCount() + 1)));
    }

    public static void callReportEvent(ProxiedPlayer player, OfflinePlayer target, String reason) {
        TSReports.getInstance().getProxy().getPluginManager().callEvent(new ReportEvent(new Report(player.getName(), player.getUniqueId(), Utils.getPlayerIp(player.getName()), target.getName(), target.getUniqueId(), Utils.getPlayerIp(target.getName()), reason, "open", TSReports.getInstance().getStorageManager().getReportsCount() + 1)));
    }

}
