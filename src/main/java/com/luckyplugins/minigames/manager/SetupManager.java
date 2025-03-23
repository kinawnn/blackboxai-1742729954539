package com.luckyplugins.minigames.manager;

import org.bukkit.entity.Player;

public class SetupManager {
    public static void setupLobby(Player player) {
        // Logic to set up the lobby for the player
        WorldEditManager.loadSchematic("lobby_schematic", player);
    }

    public static void setupMap(Player player, String mapName) {
        // Logic to set up the specific map for the player
        WorldEditManager.loadSchematic(mapName, player);
    }
}