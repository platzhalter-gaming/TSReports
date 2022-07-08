package org.mpdev.projects.tsreports.objects;

public enum Status {
    NEW("New"),
    WIP("WIP"),
    COMPLETE("Complete");

    private final String statusName;
    Status(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

}
