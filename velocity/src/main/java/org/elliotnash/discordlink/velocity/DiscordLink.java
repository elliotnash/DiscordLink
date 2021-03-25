package org.elliotnash.discordlink.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "discordlink",
        name = "DiscordLink",
        version = "1.0-SNAPSHOT",
        description = "A plugin to link a discord channel to minecraft",
        authors = {"Elliot Nash"}
)
public class DiscordLink {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        System.out.println("HELLO INITIALIZATION");
    }
}
