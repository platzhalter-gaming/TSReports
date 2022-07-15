package org.mpdev.projects.tsreports.utils;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.mpdev.projects.tsreports.TSReports;

import java.util.*;

public class PluginHelp {

    private static final TSReports plugin = TSReports.getInstance();

    private static List<String> adminCommands;
    private static List<String> reportCommands;

    public static Map<String, Boolean> setupCommands() {
        adminCommands = Arrays.asList("login, logout, gui, clear, reload, claim, get, delete, list, status-admin, addperm".split(", "));
        reportCommands = Arrays.asList("status-player, report, language".split(", "));

        Map<String, Boolean> commands = new HashMap<>();
        adminCommands.forEach(s -> commands.put(s, plugin.getConfig().getBoolean("commands." + s)));
        reportCommands.forEach(s -> commands.put(s, plugin.getConfig().getBoolean("commands." + s)));
        plugin.getLogger().info(commands.size() + " commands have been loaded.");
        return commands;
    }

    public static void adminHelp(CommandSender sender) {
        Utils.sendText(sender, "adminHelp.above");
        Utils.sendText(sender, "adminHelp.optional");
        Utils.sendText(sender, "adminHelp.required");
        sender.sendMessage(new TextComponent(" "));
        for (Map.Entry<String, Boolean> entry : plugin.getCommands().entrySet()) {
            if (entry.getValue()) {
                if (reportCommands.contains(entry.getKey())) continue;
                Utils.sendText(sender, "command-usages." + entry.getKey());
            }
        }
        Utils.sendText(sender, "adminHelp.below");
    }

    public static void reportHelp(CommandSender sender) {
        Utils.sendText(sender, "reportHelp.above");
        Utils.sendText(sender, "reportHelp.optional");
        Utils.sendText(sender, "reportHelp.required");
        sender.sendMessage(new TextComponent(" "));
        for (Map.Entry<String, Boolean> entry : plugin.getCommands().entrySet()) {
            if (entry.getValue()) {
                if (adminCommands.contains(entry.getKey())) continue;
                Utils.sendText(sender, "command-usages." + entry.getKey());
            }
        }
        Utils.sendText(sender, "reportHelp.below");
    }

}
