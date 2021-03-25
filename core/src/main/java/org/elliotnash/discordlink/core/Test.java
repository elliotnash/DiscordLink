package org.elliotnash.discordlink.core;

import org.elliotnash.discordlink.core.config.ConfigManager;

import javax.security.auth.login.LoginException;

public class Test implements DiscordEventListener {

    private static DiscordClient client;

    public static void main(String[] args) throws LoginException {
        ConfigManager config = new ConfigManager("/Users/elliot/Desktop/test.toml");
        config.read();
        client = new DiscordClient(new Test(), config.getToken(), config.getChannel(), config.use2dAvatars());
        client.run();
    }

    @Override
    public void onReady() {
        client.send("Server has started");
//        client.send("this is ethemoose skin", "Ethemoose", "1675fff9-9598-4397-96ab-58a50fa882d6");
//        client.send("this is evanonoujtoihjasndia skin", "evanoianosksadn", "2e4a3cf0-ed09-47ad-a56c-85361ead9836");
    }

    @Override
    public void onMessage(String message, String username) {
        System.out.println(username+" said: "+message);
    }
}
