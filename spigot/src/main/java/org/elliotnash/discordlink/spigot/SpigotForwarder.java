package org.elliotnash.discordlink.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class SpigotForwarder implements Listener {

    private final Plugin plugin;
    private final String deathChannel;

    SpigotForwarder(Plugin plugin, String deathChannel){
        this.plugin = plugin;
        this.deathChannel = deathChannel;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event){
        if (event.getDeathMessage() != null) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            // write player uuid
            out.writeLong(event.getEntity().getUniqueId().getMostSignificantBits());
            out.writeLong(event.getEntity().getUniqueId().getLeastSignificantBits());
            // write death message
            out.writeUTF(event.getDeathMessage());
            // send packet
            event.getEntity().sendPluginMessage(plugin, deathChannel, out.toByteArray());
        }
    }
}
