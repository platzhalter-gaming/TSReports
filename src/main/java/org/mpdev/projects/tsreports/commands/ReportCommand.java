package org.mpdev.projects.tsreports.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.events.ReportEvent;
import org.mpdev.projects.tsreports.inventory.InventoryDrawer;
import org.mpdev.projects.tsreports.inventory.inventories.LangSelector;
import org.mpdev.projects.tsreports.inventory.inventories.Reporting;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.utils.Utils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReportCommand extends Command {

    private final TSReports plugin;
    private final int listPageLimit;
    private final int cooldown;
    private final Map<UUID, Integer> cd = new HashMap<>();

    public ReportCommand(TSReports plugin, String command, String permission, String... aliases) {
        super(command, permission, aliases);
        this.plugin = plugin;
        this.listPageLimit = plugin.getConfigManager().getConfig().getInt("listPageLimit");
        this.cooldown = plugin.getConfigManager().getConfig().getInt("reportCooldown");

        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            if (cd.isEmpty())return;
            for (UUID uuid : cd.keySet()) {
                int timeleft = cd.get(uuid);

                if (timeleft <= 0) {
                    cd.remove(uuid);
                } else {
                    cd.put(uuid, timeleft - 1);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            Utils.sendText(sender, "onlyPlayer");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("setlang")) {

            if ((!sender.hasPermission("tsreports.setlang") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (TSReports.getInstance().isProtocolize()) {

                InventoryDrawer.open(new LangSelector(null, player));
                return;

            }

            Locale oldLocale = plugin.getOfflinePlayers().get(player.getName()).getLocale();
            Locale newLocale;
            try {
                newLocale = Utils.stringToLocale(args[1]);
            } catch (Exception e) {
                Utils.sendText(player, "languageNotFound");
                return;
            }

            if (!plugin.getConfigManager().getAvailableLocales().contains(newLocale)) {
                Utils.sendText(player, "languageNotFound");
                return;
            }

            plugin.getOfflinePlayers().get(player.getName()).setLocale(newLocale);
            plugin.getStorageManager().updateLanguage(player.getUniqueId(), newLocale);

            Utils.sendText(player, "commands.interlanguage", message -> message.replace("%oldLanguage%", oldLocale.toString()).replace("%newLanguage%", newLocale.toString()));

        } else if ((args.length == 2 || args.length == 3) && args[0].equalsIgnoreCase("status")) {

            if ((!sender.hasPermission("tsreports.status") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            if (args[1].equalsIgnoreCase("list")) {

                int page;
                try {
                    page = Integer.max(Integer.parseInt(args[2]), 1);
                } catch (Exception e) {
                    page = 1;
                }
                playerStatusList(player, page);
                return;

            }

            if (!Utils.isInteger(args[1])) {
                Utils.sendText(player, "onlyNumbersAllowed");
                return;
            }

            if (args[1].length() >= 10) {
                Utils.sendText(player, "numberTooHigh");
                return;
            }

            int id = Integer.parseInt(args[1]);
            Report report = plugin.getStorageManager().getReport(id);

            if (report == null) {
                Utils.sendText(player, "reportNotFound");
                return;
            }

            Utils.sendText(player, "commands.status", message -> Utils.replaceReportPlaceholders(message, report));

        } else if (args.length >= 1) {

            if ((!sender.hasPermission("tsreports.use") || !sender.hasPermission("tsreports.admin")) && !sender.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(sender, "noPermission");
                return;
            }

            String uuidOrName = args[0];

            if ((uuidOrName.equalsIgnoreCase(player.getName()) || uuidOrName.equalsIgnoreCase(player.getUniqueId().toString())) && !player.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(player, "reportYourself");
                return;
            }

            StringBuilder reason = new StringBuilder();
            for (String arg : args) {
                if (arg.equalsIgnoreCase(args[0]))continue;
                reason.append(" ").append(arg);
            }

            if (Utils.isOnline(uuidOrName)) {

                ProxiedPlayer target = uuidOrName.length() == 36
                        ? plugin.getProxy().getPlayer(UUID.fromString(uuidOrName))
                        : plugin.getProxy().getPlayer(uuidOrName);

                if (TSReports.getInstance().isProtocolize()) {

                    InventoryDrawer.open(new Reporting(player, target));
                    return;

                }

                if (args.length == 1) {
                    Utils.sendText(player, "usages.default");
                    return;
                }

                callReportEvent(player, target, reason.toString());

            } else if (Utils.isOffline(uuidOrName)) {

                OfflinePlayer target = plugin.getOfflinePlayers().get(uuidOrName);

                if (TSReports.getInstance().isProtocolize()) {

                    InventoryDrawer.open(new Reporting(player, target));
                    return;

                }

                if (args.length == 1) {
                    Utils.sendText(player, "usages.default");
                    return;
                }

                callReportEvent(player, target, reason.toString());

            } else {

                Utils.sendText(player, "playerNotFound");
                return;

            }

            cd.put(player.getUniqueId(), cooldown);
        } else {

            Utils.sendText(player, "usages.default");

        }
    }

    public void playerStatusList(ProxiedPlayer player, int page) {
        List<Report> reports = plugin.getStorageManager().getReportsAsListFromPlayer(player.getUniqueId().toString(), page, listPageLimit);

        if (reports.isEmpty()) {
            Utils.sendText(player, "commands.listEmpty");
            return;
        }

        Utils.sendText(player, "commands.listAbove", message -> message.replace("%page%", String.valueOf(page)));

        for (Report report : reports) {
            Utils.sendText(player, "commands.listBase", message -> Utils.replaceReportPlaceholders(message, report));
        }

        Utils.sendText(player, "commands.listBelow", message -> message.replace("%page%", String.valueOf(page)));
    }

    public static void callReportEvent(ProxiedPlayer player, ProxiedPlayer target, String reason) {
        TSReports.getInstance().getProxy().getPluginManager().callEvent(new ReportEvent(new Report(player.getName(), player.getUniqueId(), Utils.getPlayerIp(player.getName()), target.getName(), target.getUniqueId(), Utils.getPlayerIp(target.getName()), reason, "flag", TSReports.getInstance().getStorageManager().getReportsCount() + 1)));
    }

    public static void callReportEvent(ProxiedPlayer player, OfflinePlayer target, String reason) {
        TSReports.getInstance().getProxy().getPluginManager().callEvent(new ReportEvent(new Report(player.getName(), player.getUniqueId(), Utils.getPlayerIp(player.getName()), target.getName(), target.getUniqueId(), Utils.getPlayerIp(target.getName()), reason, "flag", TSReports.getInstance().getStorageManager().getReportsCount() + 1)));
    }



}
