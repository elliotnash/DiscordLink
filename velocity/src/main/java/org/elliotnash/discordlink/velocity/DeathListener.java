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

public class DeathListener {

    private final DiscordClient client;
    private final ChannelIdentifier deathChannel;

    public DeathListener(DiscordClient client, ChannelIdentifier deathChannel){
        this.client = client;
        this.deathChannel = deathChannel;
    }

    UUID lastMessageUUID = null;
    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event){
        // Received plugin message, check channel identifier matches
        if(event.getIdentifier().equals(deathChannel)){
            // Since this message was meant for this listener set it to handled
            // We do this so the message doesn't get routed through.
            event.setResult(PluginMessageEvent.ForwardResult.handled());

            // Check origin of message to make sure it's not a player message
            if(event.getSource() instanceof ServerConnection){
                // Read the data written to the message
                ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
                // get message uuid
                UUID messageUUID = new UUID(in.readLong(), in.readLong());
                // if this message has already been sent, return
                if (lastMessageUUID.equals(messageUUID)) {
                    return;
                }
                // get player uuid and death message
                UUID playerUUID = new UUID(in.readLong(), in.readLong());
                String deathMessage = in.readUTF();
                // send message discord
                client.sendEmbed(deathMessage, playerUUID);
            }

        }

    }


}
