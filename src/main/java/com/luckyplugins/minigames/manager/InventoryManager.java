package com.luckyplugins.minigames.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InventoryManager implements Listener {

    public static void initialize() {
        // Register event listeners
        // PluginManager.registerEvents(new InventoryManager(), plugin);
    }

    public static void openVotingUI(Player player) {
        Inventory votingInventory = Bukkit.createInventory(null, 9, "Vote for a Map");
        
        // Add voting items
        ItemStack openVotingItem = new ItemStack(Material.PAPER);
        // Set item meta for openVotingItem (name, lore, etc.)
        
        ItemStack leaveItem = new ItemStack(Material.BARRIER);
        // Set item meta for leaveItem (name, lore, etc.)

        votingInventory.addItem(openVotingItem);
        votingInventory.addItem(leaveItem);
        
        player.openInventory(votingInventory);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Logic to check if player interacts with voting item
        // Open voting UI if the item clicked is the voting item
    }

    public static void cleanup() {
        // Cleanup logic if needed
    }
}