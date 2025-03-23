package com.luckyplugins.minigames.manager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;

public class PortalManager implements Listener {

    public static void initialize() {
        // Register event listeners
        // PluginManager.registerEvents(new PortalManager(), plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // Logic to check if player is in a portal area
        // Use coordinates from config to determine if they are in a portal
    }

    public static void cleanup() {
        // Cleanup logic if needed
    }
}