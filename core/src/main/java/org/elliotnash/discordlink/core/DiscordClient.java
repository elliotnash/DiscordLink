package org.elliotnash.discordlink.core;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordClient extends ListenerAdapter {

    public static final Integer START_COLOUR = 0x2bcf0a;
    public static final Integer STOP_COLOUR = 0xeb3e13;
    public static final Integer JOIN_COLOUR = 0x4f84ff;
    public static final Integer QUIT_COLOUR = 0x5a20c7;
    public static final Integer DEATH_COLOUR = 0xeb004a;

    private final String token;
    private final String channel_id;
    private final boolean use2dAvatars;
    private final String messageFormat;
    private final boolean allowUserPings;
    private final DiscordEventListener listener;
    private JDA jda;
    private WebhookManager webhooks;

    public DiscordClient(DiscordEventListener listener, String token, String channel_id,
                         boolean use2dAvatars, String messageFormat, boolean allowUserPings) {
        this.listener = listener;
        this.token = token;
        this.channel_id = channel_id;
        this.use2dAvatars = use2dAvatars;
        this.messageFormat = messageFormat;
        this.allowUserPings = allowUserPings;
    }
    public void run() throws LoginException {
        jda = JDABuilder.createDefault(token,
                GatewayIntent.GUILD_MEMBERS)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE).build();
        jda.addEventListener(this);
    }
    public void shutdown(){
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jda.shutdown();
    }

    GuildChannel guildChannel;
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        guildChannel = jda.getGuildChannelById(channel_id);
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
            String message = messageFormat.replaceAll("%displayname%", event.getMember().getEffectiveName());
            message = message.replaceAll("%username%", event.getAuthor().getName());
            message = message.replaceAll("%message%", event.getMessage().getContentDisplay());

            listener.onMessage(MiniMessage.get().parse(message));
        }
    }


    public void send(String message) {
        if (webhooks != null)
            webhooks.send(formatMessage(message), "System");
    }
    public void send(String message, String username, UUID uuid) {send(message, username, uuid.toString());}
    public void send(String message, String username, String uuid) {
        if (webhooks != null)
            webhooks.send(formatMessage(message), username, uuid);
    }

    public void sendEmbed(Integer colour, String message){
        if (webhooks != null)
            webhooks.sendEmbed(colour, formatMessage(message), "System");
    }
    public void sendEmbed(Integer colour, String message, UUID uuid) {
        sendEmbed(colour, message, uuid.toString());
    }
    public void sendEmbed(Integer colour, String message, String uuid) {
        if (webhooks != null)
            webhooks.sendEmbed(colour, formatMessage(message), "System", uuid);
    }

    Pattern usernamePattern = Pattern.compile("(?<=@)[^ #]{3,32}");
    public String formatMessage(String message){
        // remove at everyone pings (add zero width space)
        message = message.replaceAll("@everyone", "@\u200Beveryone");
        // if disallow user pings, add zero width space after every @
        if (!allowUserPings){
            message = message.replaceAll("@", "@\u200B");
        }
        // if allow user pings, search server to see if user with name is in server,
        // and replace @name with <@userid>
        else {
            // get possible usernames
            Matcher m = usernamePattern.matcher(message);
            while (m.find()){
                String username = m.group(0);

                BoundExtractedResult<Member> closestMatch = FuzzySearch.extractOne(username.toLowerCase(),
                        guildChannel.getMembers(), x -> x.getUser().getName()
                                .replaceAll(" ", "").toLowerCase());

                // if member exists, replace the @name with <@userid>
                System.out.println("Closest match is: "+closestMatch.getReferent());
                System.out.println("Score is: "+closestMatch.getScore());
                if (closestMatch.getScore() > 75){
                    message = message.replaceAll("@"+username,
                            "<@"+closestMatch.getReferent().getUser().getId()+">");
                }
            }
        }
        return message;
    }

}
