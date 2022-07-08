package org.mpdev.projects.tsreports.inventory.inventories;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.events.ReportEvent;
import org.mpdev.projects.tsreports.inventory.*;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.objects.Status;
import org.mpdev.projects.tsreports.utils.Messages;
import org.mpdev.projects.tsreports.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportPanel extends UIFrame {

    private final TSReports plugin = TSReports.getInstance();
    private final Object target;
    private final UUID targetUuid;

    public ReportPanel(UIFrame parent, ProxiedPlayer viewer, ProxiedPlayer target) {
        super(parent, viewer);
        this.target = target;
        this.targetUuid = target.getUniqueId();
    }

    public ReportPanel(UIFrame parent, ProxiedPlayer viewer, OfflinePlayer target) {
        super(parent, viewer);
        this.target = target;
        this.targetUuid = target.getUniqueId();
    }

    @Override
    public String getTitle() {
        return Messages.GUI_REPORTPANEL_TITLE.getString(getViewer().getName());
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

        addReason(ItemType.IRON_SWORD, Messages.GUI_REPORTPANEL_REASONS_HACKING.getString(getViewer().getName()), 10, "Hacking");
        addReason(ItemType.FEATHER, Messages.GUI_REPORTPANEL_REASONS_FLYING.getString(getViewer().getName()), 12, "Flying");
        addReason(ItemType.DIAMOND_ORE, Messages.GUI_REPORTPANEL_REASONS_XRAY.getString(getViewer().getName()), 14, "X-Ray");
        addReason(ItemType.TNT, Messages.GUI_REPORTPANEL_REASONS_GRIEFING.getString(getViewer().getName()), 16, "Griefing");
        addReason(ItemType.NAME_TAG, Messages.GUI_REPORTPANEL_REASONS_NAME.getString(getViewer().getName()), 20, "Name");
        addReason(ItemType.SKELETON_SKULL, Messages.GUI_REPORTPANEL_REASONS_SKIN.getString(getViewer().getName()), 22, "Skin");
        addReason(ItemType.PAPER, Messages.GUI_REPORTPANEL_REASONS_OTHER.getString(getViewer().getName()), 24, "Other");

        for (int i = 27; i < 35; i++) {
            add(Components.getBarrierComponent(i, getViewer()));
        }
        add(Components.getBarrierComponent(35, getViewer()));
    }

    public void addReason(ItemType type, String name, int slot, String reason) {
        UIComponent component = new UIComponentImpl.Builder(type)
                .name(name).slot(slot).build();
        component.setListener(ClickType.LEFT_CLICK, () -> {
            if (reason.equals("Other")) {
                ReasonOther reasonOther = Utils.isOnline(targetUuid.toString()) ? new ReasonOther(getViewer(), (ProxiedPlayer) target) : new ReasonOther(getViewer(), (OfflinePlayer) target);
                reasonOther.start();
                InventoryDrawer.close(this);
                return;
            }
            if (Utils.isOnline(targetUuid.toString())) {
                callEvent(getViewer(), (ProxiedPlayer) target, reason);
            } else {
                callEvent(getViewer(), (OfflinePlayer) target, reason);
            }
            InventoryDrawer.close(this);
        });
        add(component);
    }

    public void callEvent(ProxiedPlayer player, ProxiedPlayer target, String reason) {
        plugin.getProxy().getPluginManager().callEvent(new ReportEvent(new Report(target.getName(), target.getUniqueId(), Utils.getPlayerIp(target.getUniqueId()), reason, player.getName(), player.getServer().getInfo().getName(), Status.NEW, highestId())));
    }

    public void callEvent(ProxiedPlayer player, OfflinePlayer target, String reason) {
        plugin.getProxy().getPluginManager().callEvent(new ReportEvent(new Report(target.getName(), target.getUniqueId(), Utils.getPlayerIp(target.getUniqueId()), reason, player.getName(), player.getServer().getInfo().getName(), Status.NEW, highestId())));
    }

    public int highestId() {
        return plugin.getStorageManager().getReportsCount() + 1;
    }

    public static class ReasonOther implements Listener {

        public final ProxiedPlayer player;
        public final Object target;
        public final UUID targetUuid;
        public List<UUID> others = new ArrayList<>();

        public ReasonOther(ProxiedPlayer player, ProxiedPlayer target) {
            this.player = player;
            this.target = target;
            this.targetUuid = target.getUniqueId();
        }

        public ReasonOther(ProxiedPlayer player, OfflinePlayer target) {
            this.player = player;
            this.target = target;
            this.targetUuid = target.getUniqueId();
        }

        public void start() {
            TSReports plugin = TSReports.getInstance();
            plugin.getProxy().getPluginManager().registerListener(plugin, this);
            others.add(player.getUniqueId());
            Utils.sendText(player, "otherReason.message");
        }

        @EventHandler
        public void onChat(ChatEvent event) {
            if (!(event.getSender() instanceof ProxiedPlayer)) return;
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            if (!others.contains(player.getUniqueId())) return;
            event.setCancelled(true);
            if (event.getMessage().equalsIgnoreCase("cancel")) {
                others.remove(player.getUniqueId());
                Utils.sendText(player, "otherReason.cancelled");
                return;
            }

            String reason = event.getMessage();
            if (Utils.isOnline(targetUuid.toString())) {
                callEvent(player, (ProxiedPlayer) target, reason);
            } else {
                callEvent(player, (OfflinePlayer) target, reason);
            }
            others.remove(player.getUniqueId());
        }

        public void callEvent(ProxiedPlayer player, ProxiedPlayer target, String reason) {
            TSReports.getInstance().getProxy().getPluginManager().callEvent(new ReportEvent(new Report(target.getName(), target.getUniqueId(), Utils.getPlayerIp(target.getUniqueId()), reason, player.getName(), player.getServer().getInfo().getName(), Status.NEW, highestId())));
        }

        public void callEvent(ProxiedPlayer player, OfflinePlayer target, String reason) {
            TSReports.getInstance().getProxy().getPluginManager().callEvent(new ReportEvent(new Report(target.getName(), target.getUniqueId(), Utils.getPlayerIp(target.getUniqueId()), reason, player.getName(), player.getServer().getInfo().getName(), Status.NEW, highestId())));
        }

        public int highestId() {
            return TSReports.getInstance().getStorageManager().getReportsCount() + 1;
        }

    }

}
