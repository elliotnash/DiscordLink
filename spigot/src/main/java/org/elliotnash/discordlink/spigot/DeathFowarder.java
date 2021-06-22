package org.elliotnash.discordlink.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

public class DeathFowarder implements Listener {

    private final Plugin plugin;
    DeathFowarder(Plugin plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event){
        if (event.getDeathMessage() != null) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(event.getDeathMessage());
            // send packet
            Bukkit.getServer().sendPluginMessage(plugin, "discordlink:death", out.toByteArray());
        }
    }
}
