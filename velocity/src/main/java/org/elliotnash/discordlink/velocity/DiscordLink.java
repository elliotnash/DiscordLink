package org.elliotnash.discordlink.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import org.elliotnash.discordlink.core.DiscordClient;
import org.elliotnash.discordlink.core.config.ConfigManager;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;

@Plugin(
        id = "discordlink",
        name = "DiscordLink",
        version = "1.0-SNAPSHOT",
        description = "A plugin to link a discord channel to minecraft",
        authors = {"Elliot Nash"}
)
public class DiscordLink {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private ChatListener chatListener;

    @Inject
    public DiscordLink(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory){
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    private final ChannelIdentifier deathChannel =
            MinecraftChannelIdentifier.from("discordlink:death");

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        chatListener = new ChatListener(server);

        ConfigManager config = new ConfigManager(dataDirectory+"/config.toml");
        config.read();

        chatListener.client = new DiscordClient(chatListener, config.getToken(), config.getChannel(),
                config.use2dAvatars(), config.getMessageFormat(), config.getAllowUserPings());
        try {
            chatListener.client.run();
        } catch (LoginException e) {
            e.printStackTrace();
            logger.error("Failed to login, plugin is disabling");
        }

        // register the chat listener
        server.getEventManager().register(this, chatListener);

        // register the death message plugin listener
        server.getChannelRegistrar().register(deathChannel);
        server.getEventManager().register(this,
                new SpigotListener(chatListener.client, deathChannel));

    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        chatListener.client.sendEmbed(DiscordClient.STOP_COLOUR, "Server has stopped");
        chatListener.client.shutdown();
    }

}
