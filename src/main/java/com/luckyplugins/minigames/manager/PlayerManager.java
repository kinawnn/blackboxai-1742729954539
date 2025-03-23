package com.luckyplugins.minigames.manager;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private static Map<UUID, PlayerData> playerData = new HashMap<>();

    public static void addPlayer(Player player) {
        playerData.putIfAbsent(player.getUniqueId(), new PlayerData());
    }

    public static void removePlayer(Player player) {
        playerData.remove(player.getUniqueId());
    }

    public static int getPlayerPosition(Player player) {
        PlayerData data = playerData.get(player.getUniqueId());
        return data != null ? data.getCurrentPosition() : -1;
    }

    public static void setPlayerPosition(Player player, int position) {
        PlayerData data = playerData.get(player.getUniqueId());
        if (data != null) {
            data.setCurrentPosition(position);
        }
    }

    public static int getPlayerWins(Player player) {
        PlayerData data = playerData.get(player.getUniqueId());
        return data != null ? data.getWins() : 0;
    }

    public static void incrementPlayerWins(Player player) {
        PlayerData data = playerData.get(player.getUniqueId());
        if (data != null) {
            data.incrementWins();
        }
    }

    public static int getPlayerGamesPlayed(Player player) {
        PlayerData data = playerData.get(player.getUniqueId());
        return data != null ? data.getGamesPlayed() : 0;
    }

    public static void incrementGamesPlayed(Player player) {
        PlayerData data = playerData.get(player.getUniqueId());
        if (data != null) {
            data.incrementGamesPlayed();
        }
    }

    public static void clearAllData() {
        playerData.clear();
    }

    private static class PlayerData {
        private int currentPosition = -1;
        private int wins = 0;
        private int gamesPlayed = 0;

        public int getCurrentPosition() {
            return currentPosition;
        }

        public void setCurrentPosition(int position) {
            this.currentPosition = position;
        }

        public int getWins() {
            return wins;
        }

        public void incrementWins() {
            this.wins++;
        }

        public int getGamesPlayed() {
            return gamesPlayed;
        }

        public void incrementGamesPlayed() {
            this.gamesPlayed++;
        }
    }
}