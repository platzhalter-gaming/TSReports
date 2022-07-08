package org.mpdev.projects.tsreports.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.inventory.InventoryDrawer;
import org.mpdev.projects.tsreports.inventory.inventories.LangSelector;
import org.mpdev.projects.tsreports.inventory.inventories.ReportPanel;
import org.mpdev.projects.tsreports.inventory.inventories.StatusPanel;
import org.mpdev.projects.tsreports.objects.OfflinePlayer;
import org.mpdev.projects.tsreports.utils.PluginHelp;
import org.mpdev.projects.tsreports.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReportCommand extends Command {

    private final TSReports plugin;
    private final int cooldown;
    private final Map<UUID, Integer> cd = new HashMap<>();

    public ReportCommand(TSReports plugin, String command, String permission, String... aliases) {
        super(command, permission, aliases);
        this.plugin = plugin;
        this.cooldown = plugin.getConfig().getInt("reportCooldown", 60);

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

        if (args.length == 1 && args[0].equalsIgnoreCase("language")) {

            if (!TSReports.getInstance().getConfig().getBoolean("gui.languageselector")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!plugin.getCommands().get("language")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!Utils.hasPermission(player, "tsreports.language") || !Utils.hasPermission(player, "tsreports.admin")) {
                Utils.sendText(player, "noPermission");
                return;
            }

            InventoryDrawer.open(new LangSelector(null, player));

        } else if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("status")) {

            if (!TSReports.getInstance().getConfig().getBoolean("gui.statuspanel")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!plugin.getCommands().get("status-player")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!Utils.hasPermission(player, "tsreports.statuspanel") || !Utils.hasPermission(player, "tsreports.admin")) {
                Utils.sendText(player, "noPermission");
                return;
            }

            if (args.length == 1) {
                InventoryDrawer.open(new StatusPanel(null, player, 1));
                return;
            }

            if (!Utils.isInteger(args[1])) {
                Utils.sendText(player, "mustBeNumber");
                return;
            }

            int page = Integer.parseInt(args[1]);
            InventoryDrawer.open(new StatusPanel(null, player, page));

        } else if (args.length >= 1) {

            if (!plugin.getCommands().get("report")) {
                Utils.sendText(sender, "commandDisabled");
                return;
            }

            if (!Utils.hasPermission(player, "tsreports.use") || !Utils.hasPermission(player, "tsreports.admin")) {
                Utils.sendText(player, "noPermission");
                return;
            }

            if (cd.containsKey(player.getUniqueId())) {
                Utils.sendText(player, "reportCooldown", message -> message.replace("%time%", String.valueOf(cd.get(player.getUniqueId()))));
                return;
            }

            if (plugin.getConfigManager().getBannedPlayers().contains(player.getName()) || plugin.getConfigManager().getBannedPlayers().contains(player.getUniqueId().toString())) {
                Utils.sendText(sender, "blacklisted");
                return;
            }

            String uuidOrName = args[0];

            if ((uuidOrName.equalsIgnoreCase(player.getName()) || uuidOrName.equalsIgnoreCase(player.getUniqueId().toString())) && !player.getName().equalsIgnoreCase("LaurinVL")) {
                Utils.sendText(player, "reportYourself");
                return;
            }

            if (Utils.isOnline(uuidOrName)) {

                ProxiedPlayer target = uuidOrName.length() == 36
                        ? plugin.getProxy().getPlayer(UUID.fromString(uuidOrName))
                        : plugin.getProxy().getPlayer(uuidOrName);

                InventoryDrawer.open(new ReportPanel(null, player, target));

            } else if (Utils.isOffline(uuidOrName)) {

                OfflinePlayer target = TSReports.getInstance().getOfflinePlayers().get(uuidOrName);

                InventoryDrawer.open(new ReportPanel(null, player, target));

            } else {

                Utils.sendText(player, "playerNotFound");
                return;

            }

            cd.put(player.getUniqueId(), cooldown);

        } else {

            PluginHelp.reportHelp(player);

        }

    }

}
