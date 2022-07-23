package org.mpdev.projects.tsreports.managers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.events.DiscordBotReadyEvent;
import org.mpdev.projects.tsreports.objects.Report;
import org.mpdev.projects.tsreports.utils.Utils;

import javax.security.auth.login.LoginException;

public class DiscordManager {

    private final TSReports plugin = TSReports.getInstance();
    private final ConfigManager configManager = plugin.getConfigManager();
    private final Configuration config = plugin.getConfig();
    private final boolean isEnabledInConfig;
    private JDA api;
    private TextChannel announceChannel;

    public DiscordManager() {
        this.isEnabledInConfig = config.getBoolean("discord.enabled");
        if (isEnabledInConfig) {
            buildBot();
        }
    }

    private void buildBot() {
        try {
            api = JDABuilder.createDefault(config.getString("discord.token"))
                    .addEventListeners(new DiscordBotReadyEvent())
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .build();
        } catch (LoginException e) {
            plugin.getLogger().severe("Discord bot has failed to connect!");
            e.printStackTrace();
        }
    }

    public void disconnectBot() {
        if (!isEnabledInConfig) return;
        if (api != null) {
            api.shutdown();
            api = null;
        }
    }

    public void sendReportEmbed(Report report) {
        if (!isEnabledInConfig) return;
        String path = "discord.report-announce.embeds.report";
        if (!(config.getBoolean("discord.report-announce.enabled") && config.getBoolean(path))) return;
        String json = Utils.replaceReportPlaceholders(configManager.getEmbed("REPORT"), report);
        announceChannel.sendMessageEmbeds(((JDAImpl) api).getEntityBuilder().createMessageEmbed(DataObject.fromJson(json))).queue();
    }

    public void sendClaimEmbed(CommandSender player, int reportId) {
        if (!isEnabledInConfig) return;
        String path = "discord.report-announce.embeds.claim";
        if (!(config.getBoolean("discord.report-announce.enabled") && config.getBoolean(path))) return;
        String json = Utils.replaceEmbedPlaceholders(configManager.getEmbed("CLAIM"), player, reportId);
        announceChannel.sendMessageEmbeds(((JDAImpl) api).getEntityBuilder().createMessageEmbed(DataObject.fromJson(json))).queue();
    }

    public void sendDeleteEmbed(CommandSender player, int reportId) {
        if (!isEnabledInConfig) return;
        String path = "discord.report-announce.embeds.delete";
        if (!(config.getBoolean("discord.report-announce.enabled") && config.getBoolean(path))) return;
        String json = Utils.replaceEmbedPlaceholders(configManager.getEmbed("DELETE"), player, reportId);
        announceChannel.sendMessageEmbeds(((JDAImpl) api).getEntityBuilder().createMessageEmbed(DataObject.fromJson(json))).queue();
    }

    public void sendCompleteEmbed(CommandSender player, int reportId) {
        if (!isEnabledInConfig) return;
        String path = "discord.report-announce.embeds.complete";
        if (!(config.getBoolean("discord.report-announce.enabled") && config.getBoolean(path))) return;
        String json = Utils.replaceEmbedPlaceholders(configManager.getEmbed("COMPLETE"), player, reportId);
        announceChannel.sendMessageEmbeds(((JDAImpl) api).getEntityBuilder().createMessageEmbed(DataObject.fromJson(json))).queue();
    }

    public void setAnnounceChannel(TextChannel announceChannel) {
        this.announceChannel = announceChannel;
    }

    public JDA getApi() {
        return api;
    }

}
