package com.luckyplugins.minigames.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static List<Player> players = new ArrayList<>();
    private static boolean isGameRunning = false;
    private static int gameTime = 0; // Time in seconds
    private static BukkitTask gameTimer;
    private static final int GAME_DURATION = 300; // 5 minutes in seconds

    public static void initialize() {
        // Initialize game manager
    }

    public static void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            PlayerManager.addPlayer(player);
            if (shouldStartGame()) {
                startGame();
            }
        }
    }

    public static void removePlayer(Player player) {
        players.remove(player);
        if (players.isEmpty() && isGameRunning) {
            endGame();
        }
    }

    private static boolean shouldStartGame() {
        return players.size() >= ConfigManager.getMinPlayersToStart() && !isGameRunning;
    }

    private static void startGame() {
        isGameRunning = true;
        gameTime = GAME_DURATION;
        
        // Start the game timer
        gameTimer = Bukkit.getScheduler().runTaskTimer(
            Bukkit.getPluginManager().getPlugin("LuckyDropper"),
            () -> {
                if (gameTime <= 0) {
                    endGame();
                    return;
                }
                gameTime--;
                updateScoreboards();
            },
            20L, // 1 second delay
            20L  // 1 second interval
        );

        // Load map schematic
        SchemaManager.loadGameMap();
        
        Bukkit.getLogger().info("Game has started with " + players.size() + " players.");
    }

    private static void endGame() {
        isGameRunning = false;
        if (gameTimer != null) {
            gameTimer.cancel();
            gameTimer = null;
        }

        // Award rewards based on positions
        for (Player player : players) {
            int position = PlayerManager.getPlayerPosition(player);
            RewardManager.awardRewards(player, position);
            PlayerManager.incrementGamesPlayed(player);
        }

        // Reset game state
        gameTime = 0;
        updateScoreboards();
        
        Bukkit.getLogger().info("Game has ended.");
    }

    private static void updateScoreboards() {
        for (Player player : players) {
            ScoreboardManager.updateScoreboard(player);
        }
    }

    public static String getTimeLeft() {
        if (!isGameRunning) return "Not started";
        int minutes = gameTime / 60;
        int seconds = gameTime % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static int getTotalPlayers() {
        return players.size();
    }

    public static boolean isGameRunning() {
        return isGameRunning;
    }

    public static void cleanup() {
        if (gameTimer != null) {
            gameTimer.cancel();
            gameTimer = null;
        }
        players.clear();
        isGameRunning = false;
        gameTime = 0;
    }
}