package com.luckyplugins.minigames.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardManager {
    private static Map<Player, Scoreboard> playerScoreboards = new HashMap<>();
    private static FileConfiguration scoreboardConfig;
    private static int updateTaskId = -1;

    public static void initialize() {
        loadScoreboardConfig();
        startScoreboardUpdater();
    }

    private static void loadScoreboardConfig() {
        File configFile = new File(Bukkit.getPluginManager().getPlugin("LuckyDropper").getDataFolder(), "scoreboard.yml");
        scoreboardConfig = YamlConfiguration.loadConfiguration(configFile);
    }

    private static void startScoreboardUpdater() {
        int interval = scoreboardConfig.getInt("update_interval", 20);
        updateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
            Bukkit.getPluginManager().getPlugin("LuckyDropper"),
            () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateScoreboard(player);
                }
            },
            0L,
            interval
        );
    }

    public static void updateScoreboard(Player player) {
        GameStateManager.GameState state = GameStateManager.getPlayerGameState(player);
        if (!shouldShowScoreboard(state)) return;

        Scoreboard scoreboard = playerScoreboards.computeIfAbsent(player, p -> createScoreboard(p));
        Objective objective = scoreboard.getObjective("display");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("display", "dummy", 
                ChatColor.translateAlternateColorCodes('&', scoreboardConfig.getString("title")));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        // Clear existing scores
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        // Update lines
        List<String> lines = scoreboardConfig.getStringList("lines");
        int score = lines.size();
        for (String line : lines) {
            String processedLine = replacePlaceholders(line, player);
            objective.getScore(processedLine).setScore(score--);
        }

        player.setScoreboard(scoreboard);
    }

    private static boolean shouldShowScoreboard(GameStateManager.GameState state) {
        switch (state) {
            case WAITING:
                return scoreboardConfig.getBoolean("settings.show_in_lobby", true);
            case IN_PROGRESS:
                return scoreboardConfig.getBoolean("settings.show_in_game", true);
            case ENDED:
                return scoreboardConfig.getBoolean("settings.show_after_game", true);
            default:
                return true;
        }
    }

    private static Scoreboard createScoreboard(Player player) {
        return Bukkit.getScoreboardManager().getNewScoreboard();
    }

    private static String replacePlaceholders(String text, Player player) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        
        // Replace placeholders with actual values
        text = text.replace("{player_name}", player.getName());
        text = text.replace("{player_count}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("{map_name}", MapManager.getCurrentMap(player));
        text = text.replace("{votes}", String.valueOf(PlayerVoteManager.getVoteCount(PlayerVoteManager.getPlayerVote(player))));
        text = text.replace("{time_left}", GameManager.getTimeLeft());
        text = text.replace("{player_position}", String.valueOf(PlayerManager.getPlayerPosition(player)));
        text = text.replace("{total_players}", String.valueOf(GameManager.getTotalPlayers()));
        text = text.replace("{arena_name}", ArenaManager.getCurrentArena(player));
        text = text.replace("{wins}", String.valueOf(PlayerManager.getPlayerWins(player)));
        text = text.replace("{games_played}", String.valueOf(PlayerManager.getPlayerGamesPlayed(player)));

        return text;
    }

    public static void cleanup() {
        if (updateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(updateTaskId);
            updateTaskId = -1;
        }
        playerScoreboards.clear();
    }
}