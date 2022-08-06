package org.mpdev.projects.tsreports.utils;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.events.ReportEvent;
import org.mpdev.projects.tsreports.inventory.UIComponent;
import org.mpdev.projects.tsreports.inventory.UIComponentImpl;
import org.mpdev.projects.tsreports.inventory.inventories.ReportPanel;
import org.mpdev.projects.tsreports.managers.ConfigManager;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.objects.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Reasons {

    private static final TSReports plugin = TSReports.getInstance();

    public static List<UIComponent> getAll(ProxiedPlayer player, Object target, UUID targetUuid) {
        ConfigManager configManager = plugin.getConfigManager();
        List<UIComponent> components = new ArrayList<>();

        for (int i = 0; i < 35; i++) {
            Configuration section = configManager.getSection("gui.reportpanel.reasons." + i, player.getName());
            if (section != null) {
                String displayName = Utils.color(section.getString("displayName"));
                ItemType type = ItemType.valueOf(section.getString("material").toUpperCase(Locale.ENGLISH));
                int slot = section.getInt("slot");
                String reason = section.getString("reason");

                UIComponent component = new UIComponentImpl.Builder(type)
                        .name(displayName)
                        .slot(slot)
                        .build();
                component.setListener(ClickType.LEFT_CLICK, () -> {
                    if (ChatColor.stripColor(displayName).toLowerCase(Locale.ENGLISH).contains("other")) {
                        ReportPanel.ReasonOther other = Utils.isOnline(targetUuid.toString()) ? new ReportPanel.ReasonOther(player, (ProxiedPlayer) target) : new ReportPanel.ReasonOther(player, (OfflinePlayer) target);
                        Protocolize.playerProvider().player(player.getUniqueId()).closeInventory();
                        other.start();
                        return;
                    }
                    if (Utils.isOnline(targetUuid.toString())) {
                        callEvent(player, (ProxiedPlayer) target, reason);
                    } else {
                        callEvent(player, (OfflinePlayer) target, reason);
                    }
                });
                components.add(component);
            }
        }

        return components;
    }

    private static void callEvent(ProxiedPlayer player, ProxiedPlayer target, String reason) {
        plugin.getProxy().getPluginManager().callEvent(new ReportEvent(new Report(target.getName(), target.getUniqueId(), Utils.getPlayerIp(target.getUniqueId()), reason, player.getName(), player.getServer().getInfo().getName(), Status.NEW, highestId())));
    }

    private static void callEvent(ProxiedPlayer player, OfflinePlayer target, String reason) {
        plugin.getProxy().getPluginManager().callEvent(new ReportEvent(new Report(target.getName(), target.getUniqueId(), Utils.getPlayerIp(target.getUniqueId()), reason, player.getName(), player.getServer().getInfo().getName(), Status.NEW, highestId())));
    }

    private static int highestId() {
        return plugin.getStorageManager().getReportsCount() + 1;
    }

}
