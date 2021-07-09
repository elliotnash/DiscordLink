package org.elliotnash.discordlink.spigot;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import org.elliotnash.discordlink.core.DiscordClient;
import org.elliotnash.discordlink.core.config.ConfigManager;

import javax.security.auth.login.LoginException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class DiscordLink extends JavaPlugin {

    private ChatListener chatListener;
    private BukkitAudiences adventure;
    private ConfigManager config;

    private final String deathChannel = "discordlink:death";

    //TODO send start and stop messages from backend spigot servers

    @Override
    public void onEnable() {

        // read/copy config
        config = new ConfigManager(this.getDataFolder()+"/config.toml");
        config.read();

        // run if server is in backend mode to a proxy (just forward death msg)
        if (hasProxy()){
            getServer().getMessenger().registerOutgoingPluginChannel(this, deathChannel);
            getServer().getPluginManager().registerEvents(
                    new SpigotForwarder(this, deathChannel), this);
        }
        // run if server is a standalone spigot server
        else {
            //create adventure audience
            this.adventure = BukkitAudiences.create(this);

            chatListener = new ChatListener(adventure);

            chatListener.client = new DiscordClient(chatListener, config.getToken(), config.getChannel(),
                    config.use2dAvatars(), config.getMessageFormat(), config.getAllowUserPings());
            try {
                chatListener.client.run();
            } catch (LoginException e) {
                e.printStackTrace();
                this.getLogger().warning("Failed to login, plugin is disabling");
            }

            getServer().getPluginManager().registerEvents(chatListener, this);
        }

    }

    @Override
    public void onDisable() {
        if (hasProxy()){

        } else{
            chatListener.client.sendEmbedTitle(DiscordClient.STOP_COLOUR, "Server has stopped", "");
            chatListener.client.shutdown();

            //close adventure audiences on disable
            if(this.adventure != null) {
                this.adventure.close();
                this.adventure = null;
            }
        }
    }

    // todo ping bungee server to check if bungee
    private boolean hasProxy() {
        boolean isPaper = false;
        try {
            isPaper = Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData") != null;
        } catch (ClassNotFoundException ignored){}

        boolean isBungeecord = getServer().spigot().getConfig()
                .getConfigurationSection("settings")
                .getBoolean( "settings.bungeecord" );

        boolean isVelocity = false;
        if (isPaper){
            try {
                Method getPaperConfig = Server.Spigot.class.getMethod("getPaperConfig");
                YamlConfiguration paperConfig = (YamlConfiguration) getPaperConfig.invoke(Bukkit.spigot());
                isVelocity = paperConfig.getBoolean("settings.velocity-support.enabled");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
                e.printStackTrace();
            }
        }
        return isBungeecord || isVelocity;
    }

}
