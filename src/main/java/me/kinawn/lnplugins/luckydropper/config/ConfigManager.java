package me.kinawn.lnplugins.luckydropper.config;

import me.kinawn.lnplugins.luckydropper.LuckyDropper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {
    private final LuckyDropper plugin;
    private FileConfiguration config;
    private FileConfiguration messagesConfig;
    private FileConfiguration scoreboardConfig;
    private FileConfiguration mapConfig;
    
    private File configFile;
    private File messagesFile;
    private File scoreboardFile;
    private File mapFile;

    public ConfigManager(LuckyDropper plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    public void loadConfigs() {
        // Create config files if they don't exist
        configFile = new File(plugin.getDataFolder(), "config.yml");
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        scoreboardFile = new File(plugin.getDataFolder(), "scoreboard.yml");
        mapFile = new File(plugin.getDataFolder(), "map.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        if (!scoreboardFile.exists()) {
            plugin.saveResource("scoreboard.yml", false);
        }
        if (!mapFile.exists()) {
            plugin.saveResource("map.yml", false);
        }

        // Load configurations
        config = YamlConfiguration.loadConfiguration(configFile);
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
        mapConfig = YamlConfiguration.loadConfiguration(mapFile);
    }

    public void reloadConfigs() {
        loadConfigs();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml!");
            e.printStackTrace();
        }
    }

    public void saveMapConfig() {
        try {
            mapConfig.save(mapFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save map.yml!");
            e.printStackTrace();
        }
    }

    // Config getters
    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    // Messages config getters
    public String getMessage(String path) {
        return messagesConfig.getString(path);
    }

    // Scoreboard config getters
    public String getScoreboardTitle() {
        return scoreboardConfig.getString("title", "&6&lLucky Dropper");
    }

    public List<String> getScoreboardLines() {
        return scoreboardConfig.getStringList("lines");
    }

    // Map config getters
    public FileConfiguration getMapConfig() {
        return mapConfig;
    }

    // Game settings
    public int getMinPlayersToStart() {
        return config.getInt("settings.min_players", 2);
    }

    public int getMaxPlayersPerGame() {
        return config.getInt("settings.max_players", 8);
    }

    public int getGameStartCountdown() {
        return config.getInt("settings.start_countdown", 30);
    }

    public int getGameDuration() {
        return config.getInt("settings.game_duration", 300);
    }

    // Voting settings
    public int getVoteDuration() {
        return config.getInt("vote.duration", 30);
    }

    public String getVotingItemMaterial() {
        return config.getString("vote.items.voting.material", "PAPER");
    }

    public String getVotingItemName() {
        return config.getString("vote.items.voting.name", "&aVote for Map");
    }

    public String getLeaveItemMaterial() {
        return config.getString("vote.items.leave.material", "BARRIER");
    }

    public String getLeaveItemName() {
        return config.getString("vote.items.leave.name", "&cLeave Game");
    }
}