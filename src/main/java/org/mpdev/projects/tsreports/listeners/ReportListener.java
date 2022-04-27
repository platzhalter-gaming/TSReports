package org.mpdev.projects.tsreports.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.events.ReportEvent;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.utils.Utils;

import java.util.Locale;
import java.util.Objects;

public class ReportListener implements Listener {

    private final TSReports plugin;

    public ReportListener(TSReports plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onReport(ReportEvent event) {
        Report report = event.getReport();
        ProxiedPlayer operator = event.getOperator();

        // Check if the operator is blacklisted
        if (plugin.getBlacklisted().contains(operator.getName().toLowerCase(Locale.ROOT)) || plugin.getBlacklisted().contains(operator.getUniqueId().toString().toLowerCase(Locale.ROOT))) {

            Utils.sendText(operator, "blacklisted");
            return;

        }

        // Check if the target is reportable (if online)
        if (Utils.isOnline(report.getTargetUuid().toString())) {

            ProxiedPlayer target = TSReports.getInstance().getProxy().getPlayer(report.getTargetUuid());
            if (target.hasPermission("tsreports.staff") || target.hasPermission("tsreports.admin")) {
                Utils.sendText(operator, "cannotReportTarget", message -> message.replace("%target%", report.getTargetName()));
            }

        }

        // Adding report to database
        plugin.getStorageManager().addReportToReports(report);
        plugin.getStorageManager().addReportToHistory(report);

        // Sending message to operator
        Utils.sendText(operator, "commands.report", message -> Utils.replaceReportPlaceholders(message, report));

        // Contact staff members
        plugin.getOfflinePlayers().values().stream().filter(OfflinePlayer::isNotify).map(player -> plugin.getProxy().getPlayer(player.getUniqueId())).filter(Objects::nonNull).forEach(player -> {
            Utils.sendText(player, "staffNotify", message -> Utils.replaceReportPlaceholders(message, report));
        });
    }
}
