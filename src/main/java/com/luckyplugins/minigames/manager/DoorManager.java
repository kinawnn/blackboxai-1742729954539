package com.luckyplugins.minigames.manager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DoorManager implements Listener {

    public static void initialize() {
        // Register event listeners
        // PluginManager.registerEvents(new DoorManager(), plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.WOODEN_DOOR) {
            // Handle door interaction
            if (ConfigManager.isSoundEffectsEnabled()) {
                player.playSound(player.getLocation(), "door.open", 1.0f, 1.0f);
            }
            // Additional door logic here
        }
    }

    public static void cleanup() {
        // Cleanup logic if needed
    }
}