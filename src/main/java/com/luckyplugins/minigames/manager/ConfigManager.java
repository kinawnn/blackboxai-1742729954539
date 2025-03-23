package com.luckyplugins.minigames.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.List;

public class ConfigManager {
    private static FileConfiguration config;
    private static FileConfiguration scoreboardConfig;
    private static FileConfiguration mapConfig;
    private static final int DEFAULT_MIN_PLAYERS = 2;

    public static void initialize() {
        loadConfigs();
    }

    private static void loadConfigs() {
        File configFile = new File(Bukkit.getPluginManager().getPlugin("LuckyDropper").getDataFolder(), "config.yml");
        File scoreboardFile = new File(Bukkit.getPluginManager().getPlugin("LuckyDropper").getDataFolder(), "scoreboard.yml");
        File mapFile = new File(Bukkit.getPluginManager().getPlugin("LuckyDropper").getDataFolder(), "map.yml");

        config = YamlConfiguration.loadConfiguration(configFile);
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
        mapConfig = YamlConfiguration.loadConfiguration(mapFile);
    }

    // General Settings
    public static int getMinPlayersToStart() {
        return config.getInt("settings.min_players", DEFAULT_MIN_PLAYERS);
    }

    // Voting Item Settings
    public static String getVotingItemMaterial() {
        return config.getString("vote.ui.open_voting_item.material", "PAPER");
    }

    public static String getVotingItemName() {
        return config.getString("vote.ui.open_voting_item.name", "&aOpen Voting");
    }

    public static List<String> getVotingItemLore() {
        return config.getStringList("vote.ui.open_voting_item.lore");
    }

    // Leave Item Settings
    public static String getLeaveItemMaterial() {
        return config.getString("vote.ui.leave_item.material", "BARRIER");
    }

    public static String getLeaveItemName() {
        return config.getString("vote.ui.leave_item.name", "&cLeave Arena");
    }

    public static List<String> getLeaveItemLore() {
        return config.getStringList("vote.ui.leave_item.lore");
    }

    // Map Vote Item Settings
    public static String getMapVoteItemMaterial() {
        return config.getString("vote.ui.map_vote_item.material", "PAPER");
    }

    public static String getMapVoteItemNameFormat() {
        return config.getString("vote.ui.map_vote_item.name_format", "&b{map_name}");
    }

    public static List<String> getMapVoteItemLore() {
        return config.getStringList("vote.ui.map_vote_item.lore");
    }

    // Scoreboard Settings
    public static String getScoreboardTitle() {
        return scoreboardConfig.getString("title", "&6&lLucky Dropper");
    }

    public static List<String> getScoreboardLines() {
        return scoreboardConfig.getStringList("lines");
    }

    public static int getScoreboardUpdateInterval() {
        return scoreboardConfig.getInt("update_interval", 20);
    }

    public static boolean showScoreboardInLobby() {
        return scoreboardConfig.getBoolean("settings.show_in_lobby", true);
    }

    public static boolean showScoreboardInGame() {
        return scoreboardConfig.getBoolean("settings.show_in_game", true);
    }

    public static boolean showScoreboardAfterGame() {
        return scoreboardConfig.getBoolean("settings.show_after_game", true);
    }

    // Schema Settings
    public static String getLobbySchematic() {
        return config.getString("schema.lobby_schematic", "lobby");
    }

    public static String getMapSchematicFolder() {
        return config.getString("schema.map_schematic_folder", "maps");
    }

    public static boolean isAutoLoadEnabled() {
        return config.getBoolean("schema.auto_load", true);
    }

    public static int getSchematicSpacing() {
        return config.getInt("schema.spacing", 16);
    }

    public static void reload() {
        loadConfigs();
    }
}