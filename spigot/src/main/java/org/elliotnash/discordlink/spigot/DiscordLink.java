package org.elliotnash.discordlink.spigot;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

import org.elliotnash.discordlink.core.DiscordClient;
import org.elliotnash.discordlink.core.config.ConfigManager;

import javax.security.auth.login.LoginException;

public final class DiscordLink extends JavaPlugin {

    private ChatListener chatListener;
    private BukkitAudiences adventure;

    @Override
    public void onEnable() {

        //create adventure audience
        this.adventure = BukkitAudiences.create(this);

        chatListener = new ChatListener(adventure);

        ConfigManager config = new ConfigManager(this.getDataFolder()+"/config.toml");
        config.read();

        chatListener.client = new DiscordClient(chatListener, config.getToken(), config.getChannel(),
                config.use2dAvatars(), config.getMessageFormat());
        try {
            chatListener.client.run();
        } catch (LoginException e) {
            e.printStackTrace();
            this.getLogger().warning("Failed to login, plugin is disabling");
        }

        getServer().getPluginManager().registerEvents(chatListener, this);

    }

    @Override
    public void onDisable() {

        chatListener.client.sendEmbed("Server has stopped");

        //close adventure audiences on disable
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
}
