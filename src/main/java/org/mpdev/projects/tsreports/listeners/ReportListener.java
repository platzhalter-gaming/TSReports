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

import java.util.Objects;

public class ReportListener implements Listener {

    private final TSReports plugin;

    public ReportListener(TSReports plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onReport(ReportEvent event) {
        Report report = event.getReport();
        ProxiedPlayer operator = plugin.getProxy().getPlayer(report.getOperator());

        ProxiedPlayer target = plugin.getProxy().getPlayer(report.getUniqueId());
        if (target != null && Utils.hasPermission(target, "tsreports.admin")) {
            Utils.sendText(operator, "unableToReport");
            return;
        }

        // Adding reports to database
        plugin.getStorageManager().addReportToReports(report);
        plugin.getStorageManager().addReportToHistory(report);

        // Sending successfully reported message to operator
        Utils.sendText(operator, "command-messages.report", message -> message
                .replace("%player%", report.getPlayerName())
                .replace("%reason%", report.getReason()));

        // Contacting all staff members (loggedIn enabled only)
        plugin.getOfflinePlayers().values().stream().filter(OfflinePlayer::isLoggedIn).map(player -> plugin.getProxy().getPlayer(player.getUniqueId())).filter(Objects::nonNull).forEach(player -> Utils.sendText(player, "staffNotify", s -> s.replace("%target%", report.getPlayerName()).replace("%reason%", report.getReason()).replace("%operator%", report.getOperator())));

    }

}
