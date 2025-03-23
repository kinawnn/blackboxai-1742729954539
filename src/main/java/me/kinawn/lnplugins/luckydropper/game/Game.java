package me.kinawn.lnplugins.luckydropper.game;

import me.kinawn.lnplugins.luckydropper.LuckyDropper;
import me.kinawn.lnplugins.luckydropper.game.enums.GameState;
import me.kinawn.lnplugins.luckydropper.game.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Game {
    private final UUID uniqueId;
    private final LuckyDropper plugin;
    private final String mapName;
    private GameState state;
    private final Set<GamePlayer> players;
    private final Set<GamePlayer> finishedPlayers;
    private BukkitTask gameTask;
    private int countdown;
    private long gameStartTime;
    private long gameEndTime;

    public Game(LuckyDropper plugin, String mapName) {
        this.uniqueId = UUID.randomUUID();
        this.plugin = plugin;
        this.mapName = mapName;
        this.state = GameState.WAITING;
        this.players = new HashSet<>();
        this.finishedPlayers = new HashSet<>();
        this.countdown = plugin.getConfigManager().getGameStartCountdown();
    }

    public void playerFinished(GamePlayer player) {
        if (state != GameState.IN_PROGRESS || finishedPlayers.contains(player)) {
            return;
        }

        finishedPlayers.add(player);
        int position = finishedPlayers.size();
        player.setFinalPosition(position);

        // Award points based on position
        int points = calculatePoints(position);
        player.addScore(points);

        // Broadcast finish message
        String message = ChatColor.GREEN + player.getName() + " finished in position " + position + "!";
        broadcastMessage(message);

        // Check if all players finished
        if (finishedPlayers.size() >= players.size()) {
            endGame();
        }
    }

    private int calculatePoints(int position) {
        switch (position) {
            case 1:
                return 100;
            case 2:
                return 75;
            case 3:
                return 50;
            default:
                return Math.max(25, 100 - (position * 10)); // Minimum 25 points
        }
    }

    public void start() {
        if (state != GameState.WAITING) {
            return;
        }

        state = GameState.STARTING;
        gameStartTime = System.currentTimeMillis();

        // Start countdown task
        gameTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (countdown > 0) {
                if (countdown <= 10 || countdown % 10 == 0) {
                    broadcastMessage(plugin.getConfigManager().getMessage("game.start.countdown")
                        .replace("{time}", String.valueOf(countdown)));
                }
                countdown--;
            } else {
                startGame();
                gameTask.cancel();
            }
        }, 0L, 20L);
    }

    private void startGame() {
        state = GameState.IN_PROGRESS;
        broadcastMessage(plugin.getConfigManager().getMessage("game.start.started"));

        // Start game duration timer
        int gameDuration = plugin.getConfigManager().getGameDuration();
        gameTask = Bukkit.getScheduler().runTaskLater(plugin, this::endGame, gameDuration * 20L);

        // Teleport players to start
        for (GamePlayer player : players) {
            player.reset();
            player.getPlayer().teleport(plugin.getConfigManager().getMapConfig().getSpawnPoint(mapName));
        }
    }

    public void stop() {
        if (gameTask != null) {
            gameTask.cancel();
        }
        
        state = GameState.ENDED;
        gameEndTime = System.currentTimeMillis();

        // Calculate final positions and award rewards
        List<GamePlayer> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

        for (int i = 0; i < sortedPlayers.size(); i++) {
            GamePlayer player = sortedPlayers.get(i);
            int position = i + 1;
            player.setFinalPosition(position);

            // Send position message
            String message = plugin.getConfigManager().getMessage("game.end.stats")
                .replace("{position}", String.valueOf(position));
            player.sendMessage(message);

            // Award rewards
            if (position <= 3) {
                String rewardCommand = plugin.getConfigManager().getString("rewards." + getPositionName(position));
                if (rewardCommand != null && !rewardCommand.isEmpty()) {
                    rewardCommand = rewardCommand.replace("%player%", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand);
                }
            }
        }

        // Teleport players back to lobby
        players.forEach(GamePlayer::returnToLobby);
    }

    private String getPositionName(int position) {
        switch (position) {
            case 1: return "first";
            case 2: return "second";
            case 3: return "third";
            default: return "participation";
        }
    }

    public void addPlayer(GamePlayer player) {
        players.add(player);
        broadcastMessage(plugin.getConfigManager().getMessage("game.join")
            .replace("{player}", player.getName())
            .replace("{current}", String.valueOf(players.size()))
            .replace("{max}", String.valueOf(plugin.getConfigManager().getMaxPlayersPerGame())));

        if (players.size() >= plugin.getConfigManager().getMinPlayersToStart()) {
            start();
        }
    }

    public void removePlayer(GamePlayer player) {
        players.remove(player);
        finishedPlayers.remove(player);
        broadcastMessage(plugin.getConfigManager().getMessage("game.leave")
            .replace("{player}", player.getName())
            .replace("{current}", String.valueOf(players.size()))
            .replace("{max}", String.valueOf(plugin.getConfigManager().getMaxPlayersPerGame())));

        if (players.size() < plugin.getConfigManager().getMinPlayersToStart() && state == GameState.STARTING) {
            state = GameState.WAITING;
            if (gameTask != null) {
                gameTask.cancel();
            }
            countdown = plugin.getConfigManager().getGameStartCountdown();
            broadcastMessage(plugin.getConfigManager().getMessage("game.start.cancelled"));
        }
    }

    private void broadcastMessage(String message) {
        players.forEach(player -> player.sendMessage(message));
    }

    public boolean isFull() {
        return players.size() >= plugin.getConfigManager().getMaxPlayersPerGame();
    }

    // Getters
    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getMapName() {
        return mapName;
    }

    public GameState getState() {
        return state;
    }

    public Set<GamePlayer> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public long getGameDuration() {
        if (gameEndTime == 0) {
            return System.currentTimeMillis() - gameStartTime;
        }
        return gameEndTime - gameStartTime;
    }

    public Set<GamePlayer> getFinishedPlayers() {
        return Collections.unmodifiableSet(finishedPlayers);
    }
}