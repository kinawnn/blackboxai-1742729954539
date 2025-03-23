package com.luckyplugins.minigames;

import org.bukkit.plugin.java.JavaPlugin;

public class LuckyDropperPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        saveResource("map.yml", false);
        
        ConfigManager.loadConfig();
        MessageManager.loadMessages();
        SchemaManager.loadSchemas(); // Load lobby and map schematics
        
        // Initialize other managers
        DoorManager.initialize();
        DropperManager.initialize();
        ScoreboardManager.initialize();
        PortalManager.initialize();
        MapManager.initialize();
        VotingManager.initialize();
        
        // Register command executor
        getCommand("luckydropper").setExecutor(new LuckyDropperCommandExecutor(this));
    }

    @Override
    public void onDisable() {
        // Cancel all scheduled tasks and unregister listeners
        DoorManager.cleanup();
        DropperManager.cleanup();
        ScoreboardManager.cleanup();
        PortalManager.cleanup();
        MapManager.cleanup();
        VotingManager.cleanup();
        
        getLogger().info("LuckyDropper has been disabled.");
    }
}