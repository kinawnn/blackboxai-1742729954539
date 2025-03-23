package me.kinawn.lnplugins.luckydropper.voting;

import me.kinawn.lnplugins.luckydropper.LuckyDropper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class VotingManager {
    private final LuckyDropper plugin;
    private final Map<UUID, String> playerVotes;
    private final Map<String, Integer> mapVotes;
    private boolean isVotingActive;
    private int votingTaskId;

    public VotingManager(LuckyDropper plugin) {
        this.plugin = plugin;
        this.playerVotes = new HashMap<>();
        this.mapVotes = new HashMap<>();
        this.isVotingActive = false;
    }

    public void startVoting() {
        if (isVotingActive) return;
        
        isVotingActive = true;
        clearVotes();

        // Start voting timer
        int voteDuration = plugin.getConfigManager().getVoteDuration();
        votingTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, 
            this::endVoting, voteDuration * 20L);

        // Broadcast voting start
        Bukkit.broadcastMessage(ChatColor.GREEN + "Map voting has started! Use /vote to cast your vote!");
    }

    public void endVoting() {
        if (!isVotingActive) return;

        isVotingActive = false;
        Bukkit.getScheduler().cancelTask(votingTaskId);

        // Get winning map
        String winningMap = getWinningMap();
        if (winningMap != null) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "Voting has ended! The winning map is: " + winningMap);
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "Voting has ended! No votes were cast.");
        }

        clearVotes();
    }

    public void openVotingMenu(Player player) {
        List<String> availableMaps = new ArrayList<>(plugin.getConfigManager().getMapConfig().getKeys(false));
        int menuSize = ((availableMaps.size() + 8) / 9) * 9; // Round up to nearest multiple of 9
        
        Inventory menu = Bukkit.createInventory(null, menuSize, "Vote for a Map");

        for (String mapName : availableMaps) {
            ItemStack mapItem = createMapItem(mapName);
            menu.addItem(mapItem);
        }

        player.openInventory(menu);
    }

    private ItemStack createMapItem(String mapName) {
        ItemStack item = new ItemStack(Material.valueOf(plugin.getConfigManager().getString("vote.map_item_material")));
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.GREEN + mapName);
        
        List<String> lore = new ArrayList<>();
        int votes = mapVotes.getOrDefault(mapName, 0);
        lore.add(ChatColor.GRAY + "Votes: " + votes);
        meta.setLore(lore);
        
        item.setItemMeta(meta);
        return item;
    }

    public void castVote(Player player, String mapName) {
        if (!isVotingActive) {
            player.sendMessage(ChatColor.RED + "Voting is not currently active!");
            return;
        }

        // Remove previous vote if exists
        String previousVote = playerVotes.get(player.getUniqueId());
        if (previousVote != null) {
            mapVotes.put(previousVote, mapVotes.get(previousVote) - 1);
        }

        // Add new vote
        playerVotes.put(player.getUniqueId(), mapName);
        mapVotes.put(mapName, mapVotes.getOrDefault(mapName, 0) + 1);

        player.sendMessage(ChatColor.GREEN + "You voted for " + mapName + "!");
        updateVotingMenus();
    }

    private void updateVotingMenus() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().equals("Vote for a Map")) {
                openVotingMenu(player); // Refresh the menu
            }
        }
    }

    private String getWinningMap() {
        if (mapVotes.isEmpty()) return null;

        return mapVotes.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    private void clearVotes() {
        playerVotes.clear();
        mapVotes.clear();
    }

    public boolean isVotingActive() {
        return isVotingActive;
    }

    public Map<String, Integer> getVoteCounts() {
        return new HashMap<>(mapVotes);
    }

    public String getPlayerVote(Player player) {
        return playerVotes.get(player.getUniqueId());
    }
}