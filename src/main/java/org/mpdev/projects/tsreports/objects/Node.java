package org.mpdev.projects.tsreports.objects;

public enum Node {
    GUI("tsreports.gui"),
    LOGIN("tsreports.login"),
    LOGOUT("tsreports.logout"),
    CLEAR("tsreports.clear"),
    RELOAD("tsreports.reload"),
    CLAIM("tsreports.claim"),
    GET("tsreports.get"),
    DELETE("tsreports.delete"),
    LIST("tsreports.list"),
    STATUS("tsreports.status"),
    ADMIN("tsreports.admin"),
    GUI_ADMIN("tsreports.gui.admin"),
    GUI_MANAGEREPORTS("tsreports.gui.managereports"),
    AUTOLOGIN("tsreports.autologin");

    private final String permission;
    Node(String permission) {
        this.permission = permission;
    }

    public String permission() {
        return permission;
    }

}
