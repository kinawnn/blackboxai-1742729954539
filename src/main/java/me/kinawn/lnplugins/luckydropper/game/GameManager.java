package me.kinawn.lnplugins.luckydropper.game;

import me.kinawn.lnplugins.luckydropper.LuckyDropper;
import me.kinawn.lnplugins.luckydropper.game.enums.GameState;
import me.kinawn.lnplugins.luckydropper.game.player.GamePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {
    private final LuckyDropper plugin;
    private final Map<UUID, Game> games;
    private final Map<UUID, GamePlayer> gamePlayers;

    public GameManager(LuckyDropper plugin) {
        this.plugin = plugin;
        this.games = new HashMap<>();
        this.gamePlayers = new HashMap<>();
    }

    public Game createGame(String mapName) {
        Game game = new Game(plugin, mapName);
        games.put(game.getUniqueId(), game);
        return game;
    }

    public void removeGame(UUID gameId) {
        Game game = games.remove(gameId);
        if (game != null) {
            game.stop();
        }
    }

    public Game getGame(UUID gameId) {
        return games.get(gameId);
    }

    public Game getPlayerGame(Player player) {
        GamePlayer gamePlayer = gamePlayers.get(player.getUniqueId());
        return gamePlayer != null ? gamePlayer.getGame() : null;
    }

    public void addPlayerToGame(Player player, Game game) {
        GamePlayer gamePlayer = new GamePlayer(player, game);
        gamePlayers.put(player.getUniqueId(), gamePlayer);
        game.addPlayer(gamePlayer);
    }

    public void removePlayerFromGame(Player player) {
        GamePlayer gamePlayer = gamePlayers.remove(player.getUniqueId());
        if (gamePlayer != null) {
            Game game = gamePlayer.getGame();
            game.removePlayer(gamePlayer);
            
            // Check if game should be stopped
            if (game.getPlayers().isEmpty()) {
                removeGame(game.getUniqueId());
            }
        }
    }

    public GamePlayer getGamePlayer(Player player) {
        return gamePlayers.get(player.getUniqueId());
    }

    public boolean isPlayerInGame(Player player) {
        return gamePlayers.containsKey(player.getUniqueId());
    }

    public void shutdown() {
        // Stop all games
        for (Game game : games.values()) {
            game.stop();
        }
        games.clear();
        gamePlayers.clear();
    }

    public Map<UUID, Game> getGames() {
        return new HashMap<>(games);
    }

    public boolean canJoinGame(Player player) {
        if (isPlayerInGame(player)) {
            return false;
        }

        // Check if there's an available game
        for (Game game : games.values()) {
            if (game.getState() == GameState.WAITING && !game.isFull()) {
                return true;
            }
        }

        // Can create a new game if none available
        return true;
    }

    public Game findAvailableGame() {
        // Look for an existing game that's waiting for players
        for (Game game : games.values()) {
            if (game.getState() == GameState.WAITING && !game.isFull()) {
                return game;
            }
        }
        return null;
    }
}