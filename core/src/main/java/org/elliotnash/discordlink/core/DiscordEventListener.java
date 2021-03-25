package org.elliotnash.discordlink.core;

import net.kyori.adventure.text.Component;

public interface DiscordEventListener {
    void onReady();
    void onMessage(Component message);
}
