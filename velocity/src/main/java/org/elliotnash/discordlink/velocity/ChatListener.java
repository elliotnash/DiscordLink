package org.elliotnash.discordlink.velocity;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.elliotnash.discordlink.core.DiscordClient;
import org.elliotnash.discordlink.core.DiscordEventListener;

import java.util.List;
import java.util.Map;

public class ChatListener implements DiscordEventListener {

    public DiscordClient client;
    private final ProxyServer server;
    public ChatListener(ProxyServer server){
        this.server = server;
    }

    boolean dcReady = false;
    Map<String, ServerStatus> statuses = null;
    @Override
    public void onReady(){
        dcReady = true;
        sendStartup();
    }
    public void onReady(Map<String, ServerStatus> statuses){
        this.statuses = statuses;
        sendStartup();
    }
    void sendStartup(){
        // both velocity and discord are ready
        if (dcReady && statuses!=null){
            StringBuilder builder = new StringBuilder();
            for (ServerStatus status : statuses.values()){
                builder.append("\n\u200B \u200B \u200B ").append(status.name).append(" is **")
                        .append(status.online ? "online" : "offline").append("**");
            }
            client.sendEmbedTitle(DiscordClient.START_COLOUR, "Proxy has started", builder.toString());
        }
    }

    @Override
    public void onMessage(Component message){
        server.sendMessage(message);
    }

    @Subscribe(order = PostOrder.LAST)
    public void onPlayerChat(PlayerChatEvent event){
        client.send(event.getMessage(), event.getPlayer().getUsername(), event.getPlayer().getUniqueId());
    }

    @Subscribe(order = PostOrder.LAST)
    public void onJoin(PostLoginEvent event){
        client.sendEmbed(DiscordClient.JOIN_COLOUR,
                event.getPlayer().getUsername()+" joined the game",
                event.getPlayer().getUniqueId());
    }

    @Subscribe(order = PostOrder.LAST)
    public void onLeave(DisconnectEvent event){
        client.sendEmbed(DiscordClient.QUIT_COLOUR,
                event.getPlayer().getUsername()+" left the game",
                event.getPlayer().getUniqueId());
    }

}
