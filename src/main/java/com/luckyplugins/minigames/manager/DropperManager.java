package com.luckyplugins.minigames.manager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.Material;

public class DropperManager implements Listener {

    public static void initialize() {
        // Register event listeners
        // PluginManager.registerEvents(new DropperManager(), plugin);
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        // Logic for dropper activation
        if (event.getBlock().getType() == Material.DROPPER) {
            // Handle dropper logic here
            // Use ConfigManager to get drop_chance and fall_speed_modifier
        }
    }

    public static void cleanup() {
        // Cleanup logic if needed
    }
}