package org.mpdev.projects.tsreports.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.objects.Report;

public class ReportEvent extends Event implements Cancellable {

    private final Report report;
    private final ProxiedPlayer operator;

    private boolean cancelled;

    public ReportEvent(Report report) {
        this.report = report;
        this.operator = TSReports.getInstance().getProxy().getPlayer(report.getPlayerUuid());
    }

    public Report getReport() {
        return report;
    }

    public ProxiedPlayer getOperator() {
        return operator;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
