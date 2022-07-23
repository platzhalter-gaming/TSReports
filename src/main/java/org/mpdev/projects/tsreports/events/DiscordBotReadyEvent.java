package org.mpdev.projects.tsreports.events;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.managers.DiscordManager;

public class DiscordBotReadyEvent extends ListenerAdapter {

    private final TSReports plugin = TSReports.getInstance();
    private final Configuration config = plugin.getConfig();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        plugin.getLogger().info("Bot has been connected to discord with name: " + event.getJDA().getSelfUser().getName());
        DiscordManager discordManager = plugin.getDiscordManager();
        discordManager.setAnnounceChannel(event.getJDA().getTextChannelById(config.getString("discord.report-announce.channel-id")));
    }

}
