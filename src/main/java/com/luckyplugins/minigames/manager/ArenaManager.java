package com.luckyplugins.minigames.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.*;

public class ArenaManager {
    private static Map<String, Arena> arenas = new HashMap<>();
    private static Map<UUID, String> playerArenas = new HashMap<>();

    public static void initialize() {
        loadArenas();
    }

    private static void loadArenas() {
        // Load arena configurations and create Arena objects
    }

    public static String getCurrentArena(Player player) {
        return playerArenas.getOrDefault(player.getUniqueId(), "None");
    }

    public static void setPlayerArena(Player player, String arenaName) {
        if (arenas.containsKey(arenaName)) {
            playerArenas.put(player.getUniqueId(), arenaName);
        }
    }

    public static String getRewardCommand(String position) {
        switch (position.toLowerCase()) {
            case "first":
                return "alonsolevel addexp %player% 500";
            case "second":
                return "alonsolevel addexp %player% 400";
            case "third":
                return "alonsolevel addexp %player% 350";
            default:
                return "alonsolevel addexp %player% 300";
        }
    }

    public static void createArena(String name, Location lobbyLocation) {
        Arena arena = new Arena(name, lobbyLocation);
        arenas.put(name, arena);
    }

    public static void removeArena(String name) {
        arenas.remove(name);
    }

    public static Set<String> getArenaNames() {
        return arenas.keySet();
    }

    public static void cleanup() {
        arenas.clear();
        playerArenas.clear();
    }

    private static class Arena {
        private final String name;
        private final Location lobbyLocation;
        private final List<String> maps;
        private String currentMap;
        private GameState state;

        public Arena(String name, Location lobbyLocation) {
            this.name = name;
            this.lobbyLocation = lobbyLocation;
            this.maps = new ArrayList<>();
            this.state = GameState.WAITING;
        }

        public void addMap(String mapName) {
            if (maps.size() < 5) {
                maps.add(mapName);
            }
        }

        public void setCurrentMap(String mapName) {
            if (maps.contains(mapName)) {
                this.currentMap = mapName;
            }
        }

        public String getCurrentMap() {
            return currentMap;
        }

        public Location getLobbyLocation() {
            return lobbyLocation;
        }

        public List<String> getMaps() {
            return new ArrayList<>(maps);
        }

        public GameState getState() {
            return state;
        }

        public void setState(GameState state) {
            this.state = state;
        }
    }

    public enum GameState {
        WAITING,
        VOTING,
        STARTING,
        IN_PROGRESS,
        ENDING
    }
}