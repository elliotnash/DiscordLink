package org.elliotnash.discordlink.core.config;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
        configFile.getParentFile().mkdirs();
        try {
            Toml toml = new Toml().read(configFile);
            config = toml.to(Config.class);
        } catch (Exception e){
            return Optional.of(e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    public String getToken(){
        return config.discordToken;
    }

    public String getChannel(){
        return config.channelID;
    }

}

class Config {
    String discordToken;
    String channelID;
}