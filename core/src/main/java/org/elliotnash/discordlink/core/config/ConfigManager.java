package org.elliotnash.discordlink.core.config;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.InputMismatchException;
import java.util.Optional;

public class ConfigManager {
    private Config config;
    private final File configFile;

    public ConfigManager(String configPath) {
        this(new File(configPath));
    }
    public ConfigManager(Path configPath) {
        this(configPath.toFile());
    }
    public ConfigManager(File configFile) {
        this.configFile = configFile;
    }

    public Optional<String> read(){
        // make directory if not exists, and if config file doesn't exist copy default
        configFile.getParentFile().mkdirs();
        if (!configFile.exists())
            copyDefaultConfig();

        try {
            Toml toml = new Toml().read(configFile);
            config = toml.to(Config.class);
        } catch (Exception e){
            return Optional.of(e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    private void copyDefaultConfig(){

        try {
            Files.copy(getClass().getResourceAsStream("/config.toml"), configFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getToken(){
        return config.discordToken;
    }

    public String getChannel(){
        return config.channelID;
    }

    public boolean use2dAvatars(){
        return config.use2dAvatars;
    }

}

class Config {
    String discordToken;
    String channelID;
    boolean use2dAvatars;
    int configVersion;
}