package org.elliotnash.discordlink.core;

public interface DiscordEventListener {
    void onReady();
    void onMessage(String message, String username);
}
