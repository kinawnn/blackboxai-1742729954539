package com.luckyplugins.minigames.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class VotingManager implements Listener {
    private static final String VOTING_INVENTORY_TITLE = "Vote for a Map";
    private static final int VOTING_INVENTORY_SIZE = 27; // 3 rows

    public static void initialize() {
        // Register event listeners
    }

    public static void giveVotingItem(Player player) {
        if (GameStateManager.getPlayerGameState(player) == GameStateManager.GameState.WAITING) {
            ItemStack votingItem = createVotingItem();
            ItemStack leaveItem = createLeaveItem();
            
            player.getInventory().setItem(0, votingItem);
            player.getInventory().setItem(8, leaveItem);
        }
    }

    private static ItemStack createVotingItem() {
        ItemStack item = new ItemStack(Material.valueOf(ConfigManager.getVotingItemMaterial()));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigManager.getVotingItemName()));
        List<String> lore = new ArrayList<>();
        for (String loreLine : ConfigManager.getVotingItemLore()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createLeaveItem() {
        ItemStack item = new ItemStack(Material.valueOf(ConfigManager.getLeaveItemMaterial()));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLeaveItemName()));
        List<String> lore = new ArrayList<>();
        for (String loreLine : ConfigManager.getLeaveItemLore()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static void openVotingUI(Player player) {
        Inventory votingInventory = Bukkit.createInventory(null, VOTING_INVENTORY_SIZE, VOTING_INVENTORY_TITLE);
        
        // Get available maps for the player's current arena
        String arenaName = ArenaManager.getCurrentArena(player);
        List<String> availableMaps = ArenaManager.getArenaMaps(arenaName);
        
        int slot = 10; // Start from second row, second column
        for (String mapName : availableMaps) {
            ItemStack mapItem = createMapVoteItem(mapName);
            votingInventory.setItem(slot++, mapItem);
        }
        
        player.openInventory(votingInventory);
    }

    private static ItemStack createMapVoteItem(String mapName) {
        ItemStack item = new ItemStack(Material.valueOf(ConfigManager.getMapVoteItemMaterial()));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
            ConfigManager.getMapVoteItemNameFormat().replace("{map_name}", mapName)));
        
        List<String> lore = new ArrayList<>();
        int votes = PlayerVoteManager.getVoteCount(mapName);
        for (String loreLine : ConfigManager.getMapVoteItemLore()) {
            loreLine = loreLine.replace("{votes}", String.valueOf(votes));
            lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null) return;
        
        if (isVotingItem(item)) {
            event.setCancelled(true);
            openVotingUI(player);
        } else if (isLeaveItem(item)) {
            event.setCancelled(true);
            GameManager.removePlayer(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals(VOTING_INVENTORY_TITLE)) return;
        
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;
        
        String mapName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
        PlayerVoteManager.setPlayerVote(player, mapName);
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            MessageManager.getMessage("vote.success").replace("{map_name}", mapName)));
        
        player.closeInventory();
    }

    private static boolean isVotingItem(ItemStack item) {
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getDisplayName().equals(
            ChatColor.translateAlternateColorCodes('&', ConfigManager.getVotingItemName()));
    }

    private static boolean isLeaveItem(ItemStack item) {
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getDisplayName().equals(
            ChatColor.translateAlternateColorCodes('&', ConfigManager.getLeaveItemName()));
    }

    public static void cleanup() {
        // Cleanup logic if needed
    }
}