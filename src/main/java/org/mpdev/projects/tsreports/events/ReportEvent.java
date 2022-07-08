package org.mpdev.projects.tsreports.events;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import org.mpdev.projects.tsreports.objects.Report;

public class ReportEvent extends Event implements Cancellable {

    private final Report report;
    private boolean cancelled;

    public ReportEvent(Report report) {
        this.report = report;
    }

    public Report getReport() {
        return report;
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
