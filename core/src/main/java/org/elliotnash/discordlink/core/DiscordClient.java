package org.elliotnash.discordlink.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.UUID;

public class DiscordClient extends ListenerAdapter {

    private final String token;
    private final String channel_id;
    private final boolean use2dAvatars;
    private final DiscordEventListener listener;
    private JDA jda;
    private WebhookManager webhooks;

    public DiscordClient(DiscordEventListener listener, String token, String channel_id, boolean use2dAvatars) {
        this.listener = listener;
        this.token = token;
        this.channel_id = channel_id;
        this.use2dAvatars = use2dAvatars;
    }
    public void run() throws LoginException {
        jda = JDABuilder.createDefault(token).build();
        jda.addEventListener(this);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        GuildChannel guildChannel = jda.getGuildChannelById(channel_id);
        System.out.println(guildChannel);
        if (!(guildChannel instanceof TextChannel)){
            new LoginException("Invalid channel id: "+channel_id).printStackTrace();
            return;
        }

        TextChannel channel = (TextChannel) guildChannel;

        channel.retrieveWebhooks().queue(webhookList -> {
            if (webhookList.isEmpty()){
                channel.createWebhook("DiscordLink").queue(webhookResult -> {
                    webhooks = new WebhookManager(webhookResult.getUrl(), use2dAvatars);
                    listener.onReady();
                });
            } else {
                webhooks = new WebhookManager(webhookList.get(0).getUrl(), use2dAvatars);
                listener.onReady();
            }
        });
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event){
        if (event.getTextChannel().getId().equals(channel_id) && !event.getAuthor().isBot()){
            listener.onMessage(event.getMessage().getContentDisplay(), event.getMember().getEffectiveName());
        }
    }


    public void send(String message) {
        if (webhooks != null)
            webhooks.send(message, "System");
    }
    public void send(String message, String username, UUID uuid) {send(message, username, uuid.toString());}
    public void send(String message, String username, String uuid) {
        if (webhooks != null)
            webhooks.send(message, username, uuid);
    }

    public void sendEmbed(String message){
        if (webhooks != null)
            webhooks.sendEmbed(message, "System");
    }
    public void sendEmbed(String message, UUID uuid) {sendEmbed(message, uuid.toString());}
    public void sendEmbed(String message, String uuid) {
        if (webhooks != null)
            webhooks.sendEmbed(message, "System", uuid);
    }
}
