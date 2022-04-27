package org.mpdev.projects.tsreports.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.inventory.InventoryDrawer;
import org.mpdev.projects.tsreports.inventory.inventories.MainFrame;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AdminCommand extends Command {

    private final TSReports plugin;
    private final File configFile;

    private final String[] adminCommandAliases;
    private final int listPageLimit;

    public AdminCommand(TSReports plugin, String command, String permission, String... aliases) {
        super(command, permission, aliases);
        this.plugin = plugin;
        this.adminCommandAliases = (plugin.getConfigManager().getConfig().getStringList("adminCommandAliases")).toArray(new String[0]);
        this.listPageLimit = plugin.getConfigManager().getConfig().getInt("listPageLimit");
        this.configFile = plugin.getConfigManager().getConfigFile();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("notify")) {

            if (!(sender instanceof ProxiedPlayer)) {
                Utils.sendText(sender, "onlyPlayer");
                return;
            }

            if ((!sender.hasPermission("tsreports.resetall") || !sender.hasPermission("tsreports.staff") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            OfflinePlayer player = plugin.getOfflinePlayers().get(sender.getName());

            if (player.isNotify()) {

                player.setNotify(false);
                Utils.sendText(sender, "commands.notify", message -> message.replace("%mode%", "&cdisabled&7"));
                return;

            }

            player.setNotify(true);
            Utils.sendText(sender, "commands.notify", message -> message.replace("%mode%", "&aenabled&7"));

        } else if (args.length == 1 && args[0].equalsIgnoreCase("resetall")) {

            if ((!sender.hasPermission("tsreports.resetall") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            long start = System.currentTimeMillis();
            plugin.getStorageManager().resetReports();
            long finished = (System.currentTimeMillis() - start);

            Utils.sendText(sender, "commands.resetAll", message -> message.replace("%time%", String.valueOf(finished)));

        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            if ((!sender.hasPermission("tsreports.reload") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            long start = System.currentTimeMillis();
            try {
                plugin.getConfigManager().setup();
            } catch (IOException e) {
                e.printStackTrace();
            }
            long finished = (System.currentTimeMillis() - start);

            Utils.sendText(sender, "commands.reload", message -> message.replace("%time%", String.valueOf(finished)));

        } else if (args.length == 1 && (args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("info"))) {

            sendMessage(sender, "&a&m+                                                        +");
            sendMessage(sender, String.format("&6&l%s", TSReports.getInstance().getDescription().getName()));
            sendMessage(sender, "&eThe best report plugin for your server.");
            sendMessage(sender, "&e");
            sendMessage(sender, "&eDeveloper: &bTodesstoss");
            sendMessage(sender, "&eVersion: &b" + TSReports.getInstance().getDescription().getVersion());
            sendMessage(sender, "&eStorage Provider: &b" + TSReports.getInstance().getStorageManager().getStorageProvider());
            sendMessage(sender, "&e");
            sendMessage(sender, "&a&m+                                                        +");
            sendMessage(sender, "&eUse &a/" + adminCommandAliases[0] + " help &efor help.");

        } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {

            if ((!sender.hasPermission("tsreports.notify") || !sender.hasPermission("tsreports.resetall") || !sender.hasPermission("tsreports.reload") || !sender.hasPermission("tsreports.get") || !sender.hasPermission("tsreports.list")  || !sender.hasPermission("tsreports.delete") || !sender.hasPermission("tsreports.check") || !sender.hasPermission("tsreports.teleport") || !sender.hasPermission("tsreports.getuuid") || !sender.hasPermission("tsreports.blacklist") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            Utils.sendText(sender, "usages.admin");

        } else if (args.length == 2 && args[0].equalsIgnoreCase("get")) {

            if ((!sender.hasPermission("tsreports.get") || !sender.hasPermission("tsreports.staff") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (!Utils.isInteger(args[1])) {
                Utils.sendText(sender, "onlyNumbersAllowed");
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

            Utils.sendText(sender, "commands.get", message -> Utils.replaceReportPlaceholders(message, report));

        } else if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("list")) {

            if ((!sender.hasPermission("tsreports.list") || !sender.hasPermission("tsreports.staff") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            int page;
            try {
                page = Integer.max(Integer.parseInt(args[1]), 1);
            } catch (Exception e) {
                page = 1;
            }
            listReports(sender, page);

        } else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {

            if ((!sender.hasPermission("tsreports.delete") || !sender.hasPermission("tsreports.staff") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (!Utils.isInteger(args[1])) {
                Utils.sendText(sender, "onlyNumbersAllowed");
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

            plugin.getStorageManager().deleteReport(id);
            Utils.sendText(sender, "commands.delete", message -> message.replace("%id%", String.valueOf(id)));

        } else if (args.length == 2 && args[0].equalsIgnoreCase("check")) {

            if ((!sender.hasPermission("tsreports.check") || !sender.hasPermission("tsreports.staff") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            String uuidOrName = args[1];
            checkPlayer(sender, uuidOrName);

        } else if (args.length == 2 && args[0].equalsIgnoreCase("teleport")) {

            if (!(sender instanceof ProxiedPlayer)) {
                Utils.sendText(sender, "onlyPlayer");
                return;
            }

            if ((!sender.hasPermission("tsreports.teleport") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (!Utils.isInteger(args[1])) {
                Utils.sendText(sender, "onlyNumbersAllowed");
                return;
            }

            if (args[1].length() >= 10) {
                Utils.sendText(sender, "numberTooHigh");
                return;
            }

            ProxiedPlayer player = (ProxiedPlayer) sender;
            int id = Integer.parseInt(args[1]);
            Report report = plugin.getStorageManager().getReport(id);

            if (report == null) {
                Utils.sendText(sender, "reportNotFound");
                return;
            }

            ProxiedPlayer target = plugin.getProxy().getPlayer(report.getTargetUuid());

            if (target == null) {
                Utils.sendText(sender, "playerNotFound");
                return;
            }

            player.connect(target.getServer().getInfo(), ServerConnectEvent.Reason.PLUGIN);
            Utils.sendText(player, "commands.teleport", message -> message.replace("%target%", target.getName()));

        } else if (args.length == 2 && args[0].equalsIgnoreCase("getuuid")) {

            if ((!sender.hasPermission("tsreports.getuuid") || !sender.hasPermission("tsreports.staff") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            String name = args[1];

            if (Utils.isOnline(name)) {

                ProxiedPlayer player = plugin.getProxy().getPlayer(name);
                sendUuidMessage(player.getUniqueId(), player.getName(), sender);

            } else if (Utils.isOffline(name)) {

                OfflinePlayer player = plugin.getOfflinePlayers().get(name);
                sendUuidMessage(player.getUniqueId(), player.getName(), sender);

            } else {

                Utils.sendText(sender, "playerNotFound");

            }

        } else if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("blacklist")) {

            if ((!sender.hasPermission("tsreports.blacklist") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (args.length == 1) {

                if (plugin.getBlacklisted().isEmpty()) {
                    Utils.sendText(sender, "commands.blacklistEmpty");
                    return;
                }

                Utils.sendText(sender, "commands.blacklistAbove", message -> message.replace("{0}", String.valueOf(plugin.getBlacklisted().size())));

                StringBuilder players = new StringBuilder();
                for (String s : plugin.getBlacklisted()) {
                    players.append(Utils.color(plugin.getConfigManager().getMessage("commands.blacklistBase", sender.getName())
                            .replace("%player%", s)));
                }
                TextComponent component = new TextComponent(players.toString());
                sender.sendMessage(component);

                Utils.sendText(sender, "commands.blacklistBelow", message -> message.replace("{0}", String.valueOf(plugin.getBlacklisted().size())));
                return;

            }

            String uuidOrName = args[1].toLowerCase(Locale.ROOT);

            try {
                if (plugin.getBlacklisted().contains(uuidOrName)) {

                    plugin.getBlacklisted().remove(uuidOrName);
                    plugin.getConfig().set("blacklisted", plugin.getBlacklisted().toArray());
                    ConfigurationProvider.getProvider(YamlConfiguration.class).save(plugin.getConfig(), configFile);
                    plugin.getConfigManager().setup();
                    Utils.sendText(sender, "commands.blacklist", message -> message
                            .replace("%target%", uuidOrName)
                            .replace("%type%", "&cremoved&7"));
                    return;

                }

                if (plugin.getStorageManager().getPlayer(uuidOrName) == null) {
                    Utils.sendText(sender, "playerNotFound");
                    return;
                }

                plugin.getBlacklisted().add(uuidOrName);
                plugin.getConfig().set("blacklisted", plugin.getBlacklisted().toArray());
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(plugin.getConfig(), configFile);
                plugin.getConfigManager().setup();
                Utils.sendText(sender, "commands.blacklist", message -> message
                        .replace("%target%", uuidOrName)
                        .replace("%type%", "&aadded&7"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            if ((!sender.hasPermission("tsreports.notify") || !sender.hasPermission("tsreports.resetall") || !sender.hasPermission("tsreports.reload") || !sender.hasPermission("tsreports.get") || !sender.hasPermission("tsreports.list")  || !sender.hasPermission("tsreports.delete") || !sender.hasPermission("tsreports.check") || !sender.hasPermission("tsreports.teleport") || !sender.hasPermission("tsreports.getuuid") || !sender.hasPermission("tsreports.blacklist") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (plugin.isProtocolize() && sender instanceof ProxiedPlayer) {

                InventoryDrawer.open(new MainFrame((ProxiedPlayer) sender));
                return;

            }

            Utils.sendText(sender, "usages.admin");

        }

    }

    public void sendMessage(CommandSender sender, String message) {
        TextComponent component = new TextComponent(Utils.color(message));
        sender.sendMessage(component);
    }

    @Deprecated
    public void sendUuidMessage(UUID uuid, String target, CommandSender sender) {
        TextComponent component = new TextComponent(Utils.color(plugin.getConfigManager().getMessage("commands.getuuid", sender.getName())).replace("%target%", target).replace("%uuid%", uuid.toString()));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to paste in chat").create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, uuid.toString()));
        sender.sendMessage(component);
    }

    public void checkPlayer(CommandSender sender, String uuidOrName) {
        List<Report> reports = plugin.getStorageManager().getReportsFromPlayer(uuidOrName);

        if (reports.isEmpty()) {
            Utils.sendText(sender, "playerNotFound");
            return;
        }

        String suspicion;
        if (reports.size() >= plugin.getConfig().getInt("reportsTillHigh")) {
            suspicion = "&cHIGH_SUSPICION";
        } else if (reports.size() >= plugin.getConfig().getInt("reportsTillMed")) {
            suspicion = "&eMEDIUM_SUSPICION";
        } else {
            suspicion = "&aLOW_SUSPICION";
        }

        int latestReport = 1;
        for (Report report : reports) {
            if (report.getId() > latestReport) {
                latestReport = report.getId();
            }
        }
        Report report = plugin.getStorageManager().getReport(latestReport);

        Utils.sendText(sender, "commands.check", message -> message.replace("%received%", String.valueOf(reports.size())
                .replace("%suspicion%", suspicion).replace("%player%", report.getTargetName())
                .replace("%reason%", report.getReason()).replace("%id%", String.valueOf(report.getId()))));
    }

    public void listReports(CommandSender sender, int page) {
        List<Report> reports = plugin.getStorageManager().getReportsAsList(page, listPageLimit);

        if (reports.isEmpty()) {
            Utils.sendText(sender, "commands.listEmpty");
            return;
        }

        Utils.sendText(sender, "commands.listAbove", message -> message.replace("%page%", String.valueOf(page)));

        for (Report report : reports) {
            Utils.sendText(sender, "commands.listBase", message -> Utils.replaceReportPlaceholders(message, report));
        }

        Utils.sendText(sender, "commands.listBelow", message -> message.replace("%page%", String.valueOf(page)));
    }

}
