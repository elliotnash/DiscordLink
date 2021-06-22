package org.elliotnash.discordlink.spigot;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.elliotnash.discordlink.core.DiscordClient;
import org.elliotnash.discordlink.core.DiscordEventListener;

public class ChatListener implements DiscordEventListener, Listener {

    public DiscordClient client;
    private final BukkitAudiences adventure;

    public ChatListener(BukkitAudiences adventure){
        this.adventure = adventure;
    }

    @Override
    public void onReady(){
        client.sendEmbed("Server has started");
    }

    @Override
    public void onMessage(Component message){
        adventure.all().sendMessage(message);
    }

    @EventHandler()
    public void onPlayerChat(AsyncPlayerChatEvent event){
        client.send(event.getMessage(), event.getPlayer().getName(), event.getPlayer().getUniqueId());
    }

    @EventHandler()
    public void onJoin(PlayerJoinEvent event){
        client.sendEmbed(event.getPlayer().getName()+" joined the game", event.getPlayer().getUniqueId());
    }

    @EventHandler()
    public void onLeave(PlayerQuitEvent event){
        client.sendEmbed(event.getPlayer().getName()+" left the game", event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event){
        client.sendEmbed(event.getDeathMessage(), event.getEntity().getUniqueId());
    }

}
