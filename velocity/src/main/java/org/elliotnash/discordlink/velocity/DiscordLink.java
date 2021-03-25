package org.elliotnash.discordlink.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.elliotnash.discordlink.core.DiscordClient;
import org.elliotnash.discordlink.core.DiscordEventListener;
import org.elliotnash.discordlink.core.Test;
import org.elliotnash.discordlink.core.config.ConfigManager;
import org.slf4j.Logger;

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

    @Inject
    public DiscordLink(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory){
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        ChatListener chatListener = new ChatListener(server);

        ConfigManager config = new ConfigManager(dataDirectory+"/config.toml");
        config.read();

        chatListener.client = new DiscordClient(chatListener, config.getToken(), config.getChannel(), config.use2dAvatars());
        try {
            chatListener.client.run();
        } catch (LoginException e) {
            e.printStackTrace();
            logger.error("Failed to login, plugin is disabling");
        }

        server.getEventManager().register(this, chatListener);

    }

}
