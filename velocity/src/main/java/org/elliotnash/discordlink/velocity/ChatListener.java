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

public class ChatListener implements DiscordEventListener {

    public DiscordClient client;
    private final ProxyServer server;
    public ChatListener(ProxyServer server){
        this.server = server;
    }

    @Override
    public void onReady(){
        client.sendEmbed("Server has started");
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
        client.sendEmbed(event.getPlayer().getUsername()+" joined the game", event.getPlayer().getUniqueId());
    }

    @Subscribe(order = PostOrder.LAST)
    public void onLeave(DisconnectEvent event){
        client.sendEmbed(event.getPlayer().getUsername()+" left the game", event.getPlayer().getUniqueId());
    }

}
