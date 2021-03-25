package org.elliotnash.discordlink.core;

import org.elliotnash.discordlink.core.config.ConfigManager;

public class Test {
    public static void main(String[] args) {
        ConfigManager config = new ConfigManager("/Users/elliot/Desktop/test.toml");
        config.read();
        System.out.println(config.getToken());
        System.out.println(config.getChannel());
    }
}
