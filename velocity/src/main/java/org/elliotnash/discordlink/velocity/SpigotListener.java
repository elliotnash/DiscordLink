package org.elliotnash.discordlink.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import org.elliotnash.discordlink.core.DiscordClient;

import java.util.UUID;

public class SpigotListener {

    private final DiscordClient client;
    private final ChannelIdentifier deathChannel;
    private final ChannelIdentifier spigotStartupChannel;

    public SpigotListener(DiscordClient client, ChannelIdentifier deathChannel, ChannelIdentifier spigotStartupChannel){
        this.client = client;
        this.deathChannel = deathChannel;
        this.spigotStartupChannel = spigotStartupChannel;
    }

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event){
        // Received plugin message, check if death message or spigot shutdown
        if (event.getIdentifier().equals(deathChannel)){
            // Since this message was meant for this listener set it to handled
            // We do this so the message doesn't get routed through.
            event.setResult(PluginMessageEvent.ForwardResult.handled());

            // Check origin of message to make sure it's not a player message
            if (event.getSource() instanceof ServerConnection){
                // Read the data written to the message
                ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
                // get player uuid and death message
                UUID playerUUID = new UUID(in.readLong(), in.readLong());
                String deathMessage = in.readUTF();
                // send message discord
                client.sendEmbed(DiscordClient.DEATH_COLOUR, deathMessage, playerUUID);
            }

        } else if (event.getIdentifier().equals(spigotStartupChannel)){
            event.setResult(PluginMessageEvent.ForwardResult.handled());
            if (event.getSource() instanceof ServerConnection){
                // startup is true if it's starting up, false when shutting down
                boolean startup = event.getData()[0] != 0;
                // send an embed depending on whether it's started or stopped
                client.sendEmbed(DiscordClient.STOP_COLOUR,
                        ((ServerConnection) event.getSource()).getServerInfo().getName()+" server has "+
                        (startup ? "started":"stopped"));
            }
        }
    }

}
