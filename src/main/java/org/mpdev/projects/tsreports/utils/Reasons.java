package org.mpdev.projects.tsreports.utils;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
            String path = "gui.reportpanel.reasons." + i;

            String displayName = configManager.getMessage(path + ".displayName", player.getName());
            ItemType type = ItemType.valueOf(configManager.getMessage(path + ".material", player.getName()).toUpperCase(Locale.ENGLISH));
            int slot = configManager.getInteger(path + ".slot", player.getName());
            String reason = configManager.getMessage(path + ".reason");

            UIComponent component = new UIComponentImpl.Builder(type)
                    .name(displayName)
                    .slot(slot)
                    .build();
            component.setListener(ClickType.LEFT_CLICK, () -> {
                if (ChatColor.stripColor(displayName).toLowerCase(Locale.ENGLISH).contains("other")) {
                    ReportPanel.Other other = Utils.isOnline(targetUuid.toString())
                            ? new ReportPanel.Other(player, (ProxiedPlayer) target)
                            : new ReportPanel.Other(player, (OfflinePlayer) target);
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
