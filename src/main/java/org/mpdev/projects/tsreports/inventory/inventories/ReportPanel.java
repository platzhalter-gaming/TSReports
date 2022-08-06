package org.mpdev.projects.tsreports.inventory.inventories;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.events.ReportEvent;
import org.mpdev.projects.tsreports.inventory.Components;
import org.mpdev.projects.tsreports.inventory.UIComponent;
import org.mpdev.projects.tsreports.inventory.UIFrame;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.objects.Status;
import org.mpdev.projects.tsreports.utils.Messages;
import org.mpdev.projects.tsreports.utils.Reasons;
import org.mpdev.projects.tsreports.utils.Utils;

import java.util.HashSet;
import java.util.Set;
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
        if (plugin.getConfig().getBoolean("gui.reportpanel.barrier")) {
            for (int i = 0; i < 8; i++) {
                add(Components.getBarrierComponent(i, getViewer()));
            }
            add(Components.getBarrierComponent(8, getViewer()));
            for (int i = 27; i < 35; i++) {
                add(Components.getBarrierComponent(i, getViewer()));
            }
        }

        Reasons.getAll(getViewer(), target, targetUuid).forEach(this::add);
        add(Components.getBackComponent(getParent(), 35, getViewer()));
    }

    public static class Other implements Listener {

        private final Set<UUID> otherReason = new HashSet<>();

        private final ProxiedPlayer player;
        private final Object target;
        private final UUID targetUuid;

        public Other(ProxiedPlayer player, ProxiedPlayer target) {
            this.player = player;
            this.target = target;
            this.targetUuid = target.getUniqueId();
        }

        public Other(ProxiedPlayer player, OfflinePlayer target) {
            this.player = player;
            this.target = target;
            this.targetUuid = target.getUniqueId();
        }

        public void start() {
            otherReason.add(player.getUniqueId());
            Utils.sendText(player, "otherReason.message");
        }

        @EventHandler
        public void onChat(ChatEvent event) {
            if (!(event.getSender() instanceof ProxiedPlayer)) return;
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            if (!otherReason.contains(player.getUniqueId())) return;
            event.setCancelled(true);

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                otherReason.remove(player.getUniqueId());
                Utils.sendText(player, "otherReason.cancelled");
                return;
            }

            String reason = event.getMessage();
            if (Utils.isOnline(targetUuid.toString())) {
                callEvent(player, (ProxiedPlayer) target, reason);
            } else {
                callEvent(player, (OfflinePlayer) target, reason);
            }
            otherReason.remove(player.getUniqueId());
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
