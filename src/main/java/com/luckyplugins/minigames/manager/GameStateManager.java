package com.luckyplugins.minigames.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GameStateManager {
    private static Map<Player, GameState> playerGameStates = new HashMap<>();

    public static void setPlayerGameState(Player player, GameState state) {
        playerGameStates.put(player, state);
    }

    public static GameState getPlayerGameState(Player player) {
        return playerGameStates.getOrDefault(player, GameState.WAITING);
    }

    public static void clearPlayerGameState(Player player) {
        playerGameStates.remove(player);
    }

    public static void clearAllGameStates() {
        playerGameStates.clear();
    }

    public enum GameState {
        WAITING,
        IN_PROGRESS,
        ENDED
    }
}