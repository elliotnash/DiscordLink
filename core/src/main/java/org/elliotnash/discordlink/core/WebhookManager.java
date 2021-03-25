package org.elliotnash.discordlink.core;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.JDA;

import java.util.HashMap;

public class WebhookManager {

    private final WebhookClient client;

    private final String sysUrl3d = "https://static.wikia.nocookie.net/minecraft_gamepedia/images/a/ab/Grass_Block_%28item%29_BE3.png";
    private final String sysUrl2d = "https://static.wikia.nocookie.net/minecraft_gamepedia/images/2/22/Grass_Block_%28side_texture%29_BE3.png";
    private final String avatarUrl3d = "https://crafatar.com/renders/head/";
    private final String avatarUrl2d = "https://crafatar.com/avatars/";

    private final String sysUrl;
    private final String avatarUrl;

    public WebhookManager(String webhookUrl, boolean use2dAvatars){
        WebhookClientBuilder builder = new WebhookClientBuilder(webhookUrl); // or id, token
        client = builder.build();
        sysUrl = use2dAvatars ? sysUrl2d : sysUrl3d;
        avatarUrl = use2dAvatars ? avatarUrl2d : avatarUrl3d;
    }

    public void send(String message, String username){
        sendUrl(message, username, sysUrl);
    }
    public void send(String message, String username, String uuid){
        sendUrl(message, username, avatarUrl+uuid);
    }

    private void sendUrl(String message, String username, String avatarUrl){
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(username);
        builder.setAvatarUrl(avatarUrl);
        builder.setContent(message);
        client.send(builder.build());
    }


    public void sendEmbed(String message, String username){
        sendEmbedUrl(message, username, sysUrl);
    }
    public void sendEmbed(String message, String username, String uuid){
        sendEmbedUrl(message, username, sysUrl, avatarUrl+uuid);
    }

    private void sendEmbedUrl(String message, String username, String avatarUrl){
        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(0xFF00EE)
                .setDescription(message)
                .build();

        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(username);
        builder.setAvatarUrl(avatarUrl);
        builder.addEmbeds(embed);
        client.send(builder.build());
    }

    private void sendEmbedUrl(String message, String username, String avatar1Url, String avatar2Url){
        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(0xFF00EE)
                .setFooter(new WebhookEmbed.EmbedFooter(message, avatar2Url))
                .build();

        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(username);
        builder.setAvatarUrl(avatar1Url);
        builder.addEmbeds(embed);
        client.send(builder.build());
    }

}
