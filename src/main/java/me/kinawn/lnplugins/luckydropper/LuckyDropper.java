package me.kinawn.lnplugins.luckydropper;

import me.kinawn.lnplugins.luckydropper.commands.DropperAdminCommand;
import me.kinawn.lnplugins.luckydropper.commands.DropperCommand;
import me.kinawn.lnplugins.luckydropper.config.ConfigManager;
import me.kinawn.lnplugins.luckydropper.game.GameManager;
import me.kinawn.lnplugins.luckydropper.listeners.GameListener;
import me.kinawn.lnplugins.luckydropper.listeners.VotingListener;
import me.kinawn.lnplugins.luckydropper.voting.VotingManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LuckyDropper extends JavaPlugin {
    private static LuckyDropper instance;
    private ConfigManager configManager;
    private GameManager gameManager;
    private VotingManager votingManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        initializeManagers();

        // Register commands
        registerCommands();

        // Register listeners
        registerListeners();

        // Save default configurations
        saveDefaultConfigs();

        getLogger().info("LuckyDropper has been enabled!");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.shutdown();
        }

        getLogger().info("LuckyDropper has been disabled!");
    }

    private void initializeManagers() {
        this.configManager = new ConfigManager(this);
        this.gameManager = new GameManager(this);
        this.votingManager = new VotingManager(this);
    }

    private void registerCommands() {
        getCommand("dropper").setExecutor(new DropperCommand(this));
        getCommand("dropperadmin").setExecutor(new DropperAdminCommand(this));
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new GameListener(this), this);
        pm.registerEvents(new VotingListener(this), this);
    }

    private void saveDefaultConfigs() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        saveResource("map.yml", false);
        saveResource("scoreboard.yml", false);
    }

    public static LuckyDropper getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public VotingManager getVotingManager() {
        return votingManager;
    }
}