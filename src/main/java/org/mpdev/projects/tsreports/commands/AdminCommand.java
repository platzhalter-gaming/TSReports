package org.mpdev.projects.tsreports.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.inventory.InventoryDrawer;
import org.mpdev.projects.tsreports.inventory.inventories.MainFrame;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.objects.Status;
import org.mpdev.projects.tsreports.objects.Node;
import org.mpdev.projects.tsreports.utils.Luck;
import org.mpdev.projects.tsreports.utils.PluginHelp;
import org.mpdev.projects.tsreports.utils.Utils;

import java.util.List;
import java.util.UUID;

public class AdminCommand extends Command {

    private final TSReports plugin;
    private final int listPageLimit;

    public AdminCommand(TSReports plugin, String command, String permission, String... aliases) {
        super(command, permission, aliases);
        this.plugin = plugin;
        this.listPageLimit = plugin.getConfig().getInt("listPageLimit", 10);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("login")) {

            if (!plugin.getCommands().get("login")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!(sender instanceof ProxiedPlayer)) {
                Utils.sendText(sender, "onlyPlayer");
                return;
            }

            if (!Utils.hasPermission(sender, "tsreports.login") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            OfflinePlayer player = plugin.getOfflinePlayers().get(sender.getName());

            if (player.isLoggedIn()) {
                Utils.sendText(sender, "alreadyLoggedIn");
                return;
            }

            player.setLoggedIn(true);
            plugin.getStorageManager().updateLoggedStatus(player.getUniqueId(), true);
            Utils.sendText(sender, "command-messages.login", s -> s.replace("%loggedIn%", "&atrue&7"));

        } else if (args.length == 1 && args[0].equalsIgnoreCase("logout")) {

            if (!plugin.getCommands().get("logout")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!(sender instanceof ProxiedPlayer)) {
                Utils.sendText(sender, "onlyPlayer");
                return;
            }

            if (!Utils.hasPermission(sender, "tsreports.logout") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            OfflinePlayer player = plugin.getOfflinePlayers().get(sender.getName());

            if (!player.isLoggedIn()) {
                Utils.sendText(sender, "notLoggedIn");
                return;
            }

            player.setLoggedIn(false);
            plugin.getStorageManager().updateLoggedStatus(player.getUniqueId(), false);
            Utils.sendText(sender, "command-messages.logout", s -> s.replace("%loggedIn%", "&cfalse&7"));

        } else if (args.length == 1 && args[0].equalsIgnoreCase("gui")) {

            if (!plugin.getCommands().get("gui")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!(sender instanceof ProxiedPlayer)) {
                Utils.sendText(sender, "onlyPlayer");
                return;
            }

            if (!Utils.hasPermission(sender, "tsreports.gui") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            InventoryDrawer.open(new MainFrame((ProxiedPlayer) sender));

        } else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {

            if (!plugin.getCommands().get("clear")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!Utils.hasPermission(sender, "tsreports.clear") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            long start = System.currentTimeMillis();
            plugin.getStorageManager().deleteAllReports();
            long finished = System.currentTimeMillis() - start;

            Utils.sendText(sender, "command-messages.clear", s -> s.replace("%time%", String.valueOf(finished)));

        } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {

            if (!Utils.hasPermission(sender, "tsreports.gui") || !Utils.hasPermission(sender, "tsreports.login") || !Utils.hasPermission(sender, "tsreports.logout") || !Utils.hasPermission(sender, "tsreports.clear") || !Utils.hasPermission(sender, "tsreports.reload") || !Utils.hasPermission(sender, "tsreports.claim") || !Utils.hasPermission(sender, "tsreports.get") || !Utils.hasPermission(sender, "tsreports.delete") || !Utils.hasPermission(sender, "tsreports.list") || !Utils.hasPermission(sender, "tsreports.status") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            PluginHelp.adminHelp(sender);

        } else if (args.length == 1 && args[0].equalsIgnoreCase("about")) {

            if (!plugin.getCommands().get("about")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            sendMessage(sender, "&a&m+                                                        +");
            sendMessage(sender, String.format("&6&l%s", plugin.getDescription().getName()));
            sendMessage(sender, "&eThe best report plugin for your server.");
            sendMessage(sender, "&e");
            sendMessage(sender, "&eDeveloper: &bTodesstoss");
            sendMessage(sender, "&eVersion: &b" + plugin.getDescription().getVersion());
            sendMessage(sender, "&eStorage Provider: &b" + plugin.getStorageManager().getStorageProvider());
            sendMessage(sender, "&e");
            sendMessage(sender, "&a&m+                                                        +");
            sendMessage(sender, "&eUse &a/reports help &efor help.");

        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            if (!plugin.getCommands().get("reload")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!Utils.hasPermission(sender, "tsreports.reload") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            long start = System.currentTimeMillis();
            plugin.getConfigManager().setup();
            plugin.setCommands( PluginHelp.setupCommands() );
            long finished = System.currentTimeMillis() - start;

            Utils.sendText(sender, "command-messages.reload", s -> s.replace("%time%", String.valueOf(finished)));

        } else if (args.length == 2 && args[0].equalsIgnoreCase("claim")) {

            if (!plugin.getCommands().get("claim")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!(sender instanceof ProxiedPlayer)) {
                Utils.sendText(sender, "onlyPlayer");
                return;
            }

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (!Utils.hasPermission(sender, "tsreports.claim") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (!Utils.isInteger(args[1])) {
                Utils.sendText(sender, "mustBeNumber");
                return;
            }

            if (args[1].length() >= 10) {
                Utils.sendText(sender, "numberTooHigh");
                return;
            }

            int id = Integer.parseInt(args[1]);
            Report report = plugin.getStorageManager().getReport(id);

            if (report == null) {
                Utils.sendText(sender, "reportNotFound");
                return;
            }

            if (report.getClaimed() != null && (report.getClaimed().equals(player.getUniqueId()) || !report.getClaimed().equals(player.getUniqueId()))) {
                Utils.sendText(sender, "alreadyClaimed");
                return;
            }

            report.setClaimed(player.getUniqueId());
            report.setStatus(Status.WIP);
            plugin.getStorageManager().updateClaimed(id, player.getUniqueId());
            plugin.getStorageManager().updateStatus(id, Status.WIP);

            Utils.sendText(sender, "command-messages.claim", s -> s.replace("%id%", "" + id));

        } else if (args.length == 2 && args[0].equalsIgnoreCase("get")) {

            if (!plugin.getCommands().get("get")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!Utils.hasPermission(sender, "tsreports.get") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (!Utils.isInteger(args[1])) {
                Utils.sendText(sender, "mustBeNumber");
                return;
            }

            if (args[1].length() >= 10) {
                Utils.sendText(sender, "numberTooHigh");
                return;
            }

            int id = Integer.parseInt(args[1]);
            Report report = plugin.getStorageManager().getReport(id);

            if (report == null) {
                Utils.sendText(sender, "reportNotFound");
                return;
            }

            Utils.sendText(sender, "command-messages.get", s -> Utils.replaceReportPlaceholders(s, report));

        } else if (args.length == 2 && (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove"))) {

            if (!plugin.getCommands().get("delete")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!Utils.hasPermission(sender, "tsreports.delete") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (!Utils.isInteger(args[1])) {
                Utils.sendText(sender, "mustBeNumber");
                return;
            }

            if (args[1].length() >= 10) {
                Utils.sendText(sender, "numberTooHigh");
                return;
            }

            int id = Integer.parseInt(args[1]);
            Report report = plugin.getStorageManager().getReport(id);

            if (report == null) {
                Utils.sendText(sender, "reportNotFound");
                return;
            }

            if (report.getClaimed() != null && !Utils.hasPermission(sender, "tsreports.admin")) {
                if (!(sender instanceof ProxiedPlayer)) {
                    Utils.sendText(sender, "cannotDeleteClaimedReport");
                    return;
                }
                if (!report.getClaimed().equals(((ProxiedPlayer) sender).getUniqueId())) {
                    Utils.sendText(sender, "cannotDeleteClaimedReport");
                    return;
                }
            }

            plugin.getStorageManager().deleteReport(id);
            Utils.sendText(sender, "command-messages.delete", s -> s.replace("%id%", "" + id));

        } else if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("list")) {

            if (!plugin.getCommands().get("list")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!Utils.hasPermission(sender, "tsreports.list") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (args.length == 1) {
                listReports(sender, 1);
                return;
            }

            if (!Utils.isInteger(args[1])) {
                Utils.sendText(sender, "mustBeNumber");
                return;
            }

            if (args[1].length() >= 10) {
                Utils.sendText(sender, "numberTooHigh");
                return;
            }

            int page = Integer.parseInt(args[1]);
            listReports(sender, page);

        } else if (args.length == 3 && args[0].equalsIgnoreCase("status")) {

            if (!plugin.getCommands().get("status-admin")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!Utils.hasPermission(sender, "tsreports.status") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (!Utils.isInteger(args[1])) {
                Utils.sendText(sender, "mustBeNumber");
                return;
            }

            if (args[1].length() >= 10) {
                Utils.sendText(sender, "numberTooHigh");
                return;
            }

            int id = Integer.parseInt(args[1]);
            Report report = plugin.getStorageManager().getReport(id);

            if (report == null) {
                Utils.sendText(sender, "reportNotFound");
                return;
            }

            Status status = getStatus(args[2]);

            if (status == null) {
                Utils.sendText(sender, "statusNotFound");
                return;
            }

            report.setStatus(status);
            plugin.getStorageManager().updateStatus(id, status);

            Utils.sendText(sender, "command-messages.status", s -> s.replace("%status%", status.getStatusName()).replace("%id%", "" + id));

        } else if ((args.length == 1 || args.length == 2 || args.length == 3) && args[0].equalsIgnoreCase("addperm")) {

            if (!plugin.getCommands().get("addperm")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!Utils.isPluginEnabled("LuckPerms") || plugin.getApi() == null) {
                Utils.sendText(sender, "luckPerms");
                return;
            }

            if (!Utils.hasPermission(sender, "tsreports.addperm") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (args.length == 1 || args.length == 2) {
                Utils.sendText(sender, "command-messages.permissionNodes");
                return;
            }

            Node permissionNode = getPermissionNode(args[1]);

            if (permissionNode == null) {
                Utils.sendText(sender, "nodeNotFound");
                return;
            }

            String uuidOrName = args[2];

            if (Utils.isOnline(uuidOrName)) {

                ProxiedPlayer target = uuidOrName.length() == 36
                        ? plugin.getProxy().getPlayer(UUID.fromString(uuidOrName))
                        : plugin.getProxy().getPlayer(uuidOrName);

                Luck.addPermission(target.getUniqueId(), permissionNode);

                Utils.sendText(sender, "command-messages.addperm", s -> s
                        .replace("%player%", target.getName())
                        .replace("%permission%", permissionNode.permission()));

            } else {

                Utils.sendText(sender, "playerNotFound");

            }

        } else {

            if (!Utils.hasPermission(sender, "tsreports.gui") || !Utils.hasPermission(sender, "tsreports.login") || !Utils.hasPermission(sender, "tsreports.logout") || !Utils.hasPermission(sender, "tsreports.clear") || !Utils.hasPermission(sender, "tsreports.reload") || !Utils.hasPermission(sender, "tsreports.claim") || !Utils.hasPermission(sender, "tsreports.get") || !Utils.hasPermission(sender, "tsreports.delete") || !Utils.hasPermission(sender, "tsreports.list") || !Utils.hasPermission(sender, "tsreports.status") || !Utils.hasPermission(sender, "tsreports.admin")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            PluginHelp.adminHelp(sender);

        }

    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(new TextComponent(Utils.color(message)));
    }

    public Status getStatus(String statusName) {
        Status status = null;
        switch (statusName.toLowerCase()) {
            case ("new"):
                status = Status.NEW;
                break;
            case ("wip"):
                status = Status.WIP;
                break;
            case ("complete"):
                status = Status.COMPLETE;
                break;
        }
        return status;
    }

    public Node getPermissionNode(String node) {
        Node permissionNode = null;
        switch (node.toLowerCase()) {
            case ("gui"):
                permissionNode = Node.GUI;
                break;
            case ("login"):
                permissionNode = Node.LOGIN;
                break;
            case ("logout"):
                permissionNode = Node.LOGOUT;
                break;
            case ("clear"):
                permissionNode = Node.CLEAR;
                break;
            case ("reload"):
                permissionNode = Node.RELOAD;
                break;
            case ("claim"):
                permissionNode = Node.CLAIM;
                break;
            case ("get"):
                permissionNode = Node.GET;
                break;
            case ("delete"):
                permissionNode = Node.DELETE;
                break;
            case ("list"):
                permissionNode = Node.LIST;
                break;
            case ("status"):
                permissionNode = Node.STATUS;
                break;
            case ("language"):
                permissionNode = Node.LANGUAGE;
                break;
            case ("statuspanel"):
                permissionNode = Node.STATUSPANEL;
                break;
            case ("admin"):
                permissionNode = Node.ADMIN;
                break;
            case ("gui_admin"):
                permissionNode = Node.GUI_ADMIN;
                break;
            case ("gui_managereports"):
                permissionNode = Node.GUI_MANAGEREPORTS;
                break;
            case ("autologin"):
                permissionNode = Node.AUTOLOGIN;
                break;
        }
        return permissionNode;
    }

    public void listReports(CommandSender sender, int page) {
        List<Report> reports = plugin.getStorageManager().getReportsAsList(page, listPageLimit);

        if (reports.isEmpty()) {
            Utils.sendText(sender, "command-messages.listEmpty");
            return;
        }

        Utils.sendText(sender, "command-messages.listAbove", message -> message.replace("%page%", String.valueOf(page)));

        reports.forEach(report -> {
            TextComponent component = new TextComponent(Utils.getText(sender.getName(), "command-messages.listBase", s -> Utils.replaceReportPlaceholders(s, report)));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to get more information").create()));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + plugin.getAdminCommands()[0] + " get " + report.getReportId()));
            sender.sendMessage(component);
        });

        Utils.sendText(sender, "command-messages.listBelow", message -> message.replace("%page%", String.valueOf(page)));
    }

}
